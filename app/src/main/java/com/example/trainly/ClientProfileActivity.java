package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ClientProfileActivity extends AppCompatActivity {

    TextView tvUserFullname, tvUserRole, tvGreeting;
    TextView tvStatWorkouts, tvStatCalories, tvStatWeight;
    TextView tvNotificationBadge;  // NEW: Badge for notification icon
    TextView tvInvitationBadge;

    ImageView icNotification;  // NEW: Notification bell icon
    ImageView btnLogout;

    CardView cardWorkouts, cardProgress, cardMeals, cardEditProfile, cardBMICalculator,
            cardWorkoutCalendar, cardChooseTrainer, cardTrainerInvitations;

    DatabaseHelper db;
    int traineeId;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        // bind UI
        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserFullname = findViewById(R.id.tvUserFullname);
        tvUserRole = findViewById(R.id.tvUserRole);

        tvStatWorkouts = findViewById(R.id.tvStatWorkouts);
        tvStatCalories = findViewById(R.id.tvStatCalories);
        tvStatWeight = findViewById(R.id.tvStatWeight);

        // NEW: Notification icon + badge
        icNotification = findViewById(R.id.icNotification);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);
        tvInvitationBadge = findViewById(R.id.tvInvitationBadge);
        btnLogout = findViewById(R.id.btnLogout);

        cardWorkouts = findViewById(R.id.cardWorkouts);
        cardProgress = findViewById(R.id.cardProgress);
        cardMeals = findViewById(R.id.cardMeals);
        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardBMICalculator = findViewById(R.id.cardBMICalculator);
        cardWorkoutCalendar = findViewById(R.id.cardWorkoutCalendar);
        cardChooseTrainer = findViewById(R.id.cardChooseTrainer);
        cardTrainerInvitations = findViewById(R.id.cardTrainerInvitations);

        // get user info
        String name = getIntent().getStringExtra("name");
        String role = getIntent().getStringExtra("role");
        email = getIntent().getStringExtra("email");

        if (name != null) {
            tvUserFullname.setText(name);
            tvGreeting.setText("Hi, " + name.split(" ")[0] + "!");  // First name only
        }
        if (role != null) tvUserRole.setText(role);
        if (email == null) {
            Toast.makeText(this, "Error: missing email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===== LOAD STATS =====
        db = new DatabaseHelper(this);
        traineeId = db.getUserIdByEmail(email);

        tvStatWorkouts.setText(String.valueOf(db.getCompletedWorkouts(traineeId)));
        tvStatCalories.setText(String.valueOf(db.getTotalCalories(traineeId)));
        tvStatWeight.setText(db.getUserWeightByEmail(email) + " kg");

        // NEW: Load notification badge
        loadNotificationBadge();
        loadInvitationBadge();

        // NEW: Notification icon click
        icNotification.setOnClickListener(v -> {
            Intent i = new Intent(this, NotificationActivity.class);
            i.putExtra("user_id", traineeId);
            startActivity(i);
        });

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent i = new Intent(this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            });
        }

        cardTrainerInvitations.setOnClickListener(v -> {
            Intent i = new Intent(this, TraineeInvitationsActivity.class);
            i.putExtra("traineeId", traineeId);
            startActivity(i);
        });

        // card clicks
        cardWorkouts.setOnClickListener(v -> {
            Intent i = new Intent(this, TodayWorkoutActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardProgress.setOnClickListener(v -> {
            Intent i = new Intent(this, ClientProgressActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardMeals.setOnClickListener(v -> {
            Intent i = new Intent(this, TrackMealsActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardEditProfile.setOnClickListener(v -> {
            Intent i = new Intent(this, EditClientProfileActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardBMICalculator.setOnClickListener(v -> {
            Intent i = new Intent(this, BMIActivity.class);
            startActivity(i);
        });

        cardWorkoutCalendar.setOnClickListener(v -> {
            Intent i = new Intent(this, CalendarActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardChooseTrainer.setOnClickListener(v -> {
            Intent i = new Intent(this, TrainerListActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });
    }

    // NEW: Load notification badge count
    private void loadNotificationBadge() {
        int unreadCount = db.getUnreadNotificationCount(traineeId);

        if (unreadCount > 0) {
            tvNotificationBadge.setText(String.valueOf(unreadCount));
            tvNotificationBadge.setVisibility(View.VISIBLE);
        } else {
            tvNotificationBadge.setVisibility(View.GONE);
        }
    }

    private void loadInvitationBadge() {
        int pendingInvites = db.getPendingTrainerInvitationsCount(traineeId);

        if (pendingInvites > 0) {
            tvInvitationBadge.setText(String.valueOf(pendingInvites));
            tvInvitationBadge.setVisibility(View.VISIBLE);
        } else {
            tvInvitationBadge.setVisibility(View.GONE);
        }
    }

    // NEW: Refresh badge when returning from NotificationActivity
    @Override
    protected void onResume() {
        super.onResume();
        if (db != null && traineeId != 0) {
            // Reload latest profile info (name/weight/stats) after edits
            String latestName = db.getUserNameByEmail(email);
            if (latestName != null) {
                tvUserFullname.setText(latestName);
                tvGreeting.setText("Hi, " + latestName.split(" ")[0] + "!");
            }
            tvStatWeight.setText(db.getUserWeightByEmail(email) + " kg");

            tvStatWorkouts.setText(String.valueOf(db.getCompletedWorkouts(traineeId)));
            tvStatCalories.setText(String.valueOf(db.getTotalCalories(traineeId)));

            loadNotificationBadge();
            loadInvitationBadge();
        }
    }
}
