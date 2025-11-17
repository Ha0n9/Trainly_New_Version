package com.example.trainly;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ClientProfileActivity extends AppCompatActivity {

    TextView tvUserFullname, tvUserRole;
    TextView tvStatWorkouts, tvStatCalories, tvStatWeight;
    TextView tvInvitationBadge;

    CardView cardWorkouts, cardProgress, cardMeals, cardEditProfile, cardBMICalculator,
            cardWorkoutCalendar, cardChooseTrainer, cardTrainerInvitations, cardNotification;

    DatabaseHelper db;
    int traineeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        db = new DatabaseHelper(this);

        // bind UI
        tvUserFullname = findViewById(R.id.tvUserFullname);
        tvUserRole = findViewById(R.id.tvUserRole);

        tvStatWorkouts = findViewById(R.id.tvStatWorkouts);
        tvStatCalories = findViewById(R.id.tvStatCalories);
        tvStatWeight = findViewById(R.id.tvStatWeight);
        tvInvitationBadge = findViewById(R.id.tvInvitationBadge);

        cardWorkouts = findViewById(R.id.cardWorkouts);
        cardProgress = findViewById(R.id.cardProgress);
        cardMeals = findViewById(R.id.cardMeals);
        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardBMICalculator = findViewById(R.id.cardBMICalculator);
        cardWorkoutCalendar = findViewById(R.id.cardWorkoutCalendar);
        cardChooseTrainer = findViewById(R.id.cardChooseTrainer);
        cardTrainerInvitations = findViewById(R.id.cardTrainerInvitations);
        cardNotification = findViewById(R.id.cardNotificaton);

        // get user info
        String name = getIntent().getStringExtra("name");
        String role = getIntent().getStringExtra("role");
        String email = getIntent().getStringExtra("email");

        if (name != null) tvUserFullname.setText(name);
        if (role != null) tvUserRole.setText(role);
        if (email == null) {
            Toast.makeText(this, "Error: missing email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===== LOAD STATS =====
        traineeId = db.getUserIdByEmail(email);

        tvStatWorkouts.setText(String.valueOf(db.getCompletedWorkouts(traineeId)));
        tvStatCalories.setText(String.valueOf(db.getTotalCalories(traineeId)));
        tvStatWeight.setText(db.getUserWeightByEmail(email) + " kg");

        // Load invitation badge
        loadInvitationBadge();

        // card clicks
        cardWorkouts.setOnClickListener(v -> {
            Intent i = new Intent(this, ClientWorkoutsActivity.class);
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

        cardTrainerInvitations.setOnClickListener(v -> {
            Intent i = new Intent(this, TraineeInvitationsActivity.class);
            i.putExtra("traineeId", traineeId);
            startActivity(i);
        });

        cardNotification.setOnClickListener(v -> {
            Intent i = new Intent(this, NotificationActivity.class);
            i.putExtra("user_id", traineeId);
            startActivity(i);
        });
    }

    private void loadInvitationBadge() {
        Cursor c = db.getPendingTrainerInvitations(traineeId);
        int count = 0;
        if (c != null) {
            count = c.getCount();
            c.close();
        }

        if (count > 0) {
            tvInvitationBadge.setText(String.valueOf(count));
            tvInvitationBadge.setVisibility(View.VISIBLE);
        } else {
            tvInvitationBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh invitation badge when returning
        if (traineeId != -1) {
            loadInvitationBadge();
        }
    }
}