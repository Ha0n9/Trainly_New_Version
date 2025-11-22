package com.example.trainly;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TrainerDashboardActivity extends AppCompatActivity {

    TextView tvTrainerGreeting, tvStatClients, tvStatPlans, tvStatAssigned;
    TextView tvPendingCount, tvNotificationBadge;
    CardView cardCreatePlan, cardAssignWorkout, cardViewProgress, cardSendReminder;
    CardView cardPendingRequests, cardMyTrainees, cardInviteTrainee;
    ImageView icNotification, btnLogout;

    DatabaseHelper db;
    String email;
    int trainerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_dashboard);

        db = new DatabaseHelper(this);

        // GET EMAIL FROM LOGIN
        email = getIntent().getStringExtra("email");

        initViews();
        loadTrainerInfo();
        loadStats();
        loadPendingCount();
        loadNotificationBadge();
        setupClicks();
    }

    private void initViews() {
        tvTrainerGreeting = findViewById(R.id.tvTrainerGreeting);
        tvStatClients = findViewById(R.id.tvStatClients);
        tvStatPlans = findViewById(R.id.tvStatPlans);
        tvStatAssigned = findViewById(R.id.tvStatAssigned);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvNotificationBadge = findViewById(R.id.tvNotificationBadge);

        cardCreatePlan = findViewById(R.id.cardCreatePlan);
        cardAssignWorkout = findViewById(R.id.cardAssignWorkout);
        cardViewProgress = findViewById(R.id.cardViewProgress);
        cardSendReminder = findViewById(R.id.cardSendReminder);
        cardPendingRequests = findViewById(R.id.cardPendingRequests);
        cardMyTrainees = findViewById(R.id.cardMyTrainees);
        cardInviteTrainee = findViewById(R.id.cardInviteTrainee);

        icNotification = findViewById(R.id.icNotification);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadTrainerInfo() {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT id, name FROM users WHERE email=?",
                new String[]{email}
        );
        if (c.moveToFirst()) {
            trainerId = c.getInt(0);  // Only set if has data
            String name = c.getString(1);
            tvTrainerGreeting.setText("Welcome, " + name);
        }
        c.close();
    }

    private void loadStats() {
        // Count workout plans
        Cursor c1 = db.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM workout_plans WHERE trainer_id=?",
                new String[]{String.valueOf(trainerId)}
        );
        if (c1.moveToFirst()) tvStatPlans.setText(c1.getString(0));
        c1.close();

        // Count assigned workouts
        Cursor c2 = db.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM assigned_workouts WHERE plan_id IN (SELECT id FROM workout_plans WHERE trainer_id=?)",
                new String[]{String.valueOf(trainerId)}
        );
        if (c2.moveToFirst()) tvStatAssigned.setText(c2.getString(0));
        c2.close();

        // Count trainees (unique)
        Cursor c3 = db.getReadableDatabase().rawQuery(
                "SELECT COUNT(DISTINCT trainee_id) FROM assigned_workouts WHERE plan_id IN (SELECT id FROM workout_plans WHERE trainer_id=?)",
                new String[]{String.valueOf(trainerId)}
        );
        if (c3.moveToFirst()) tvStatClients.setText(c3.getString(0));
        c3.close();
    }

    private void loadPendingCount() {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM trainer_requests WHERE trainer_id=? AND status='pending' AND initiated_by='trainee'",
                new String[]{String.valueOf(trainerId)}
        );
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            tvPendingCount.setText(count + " request" + (count != 1 ? "s" : ""));
        }
        c.close();
    }

    private void loadNotificationBadge() {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM notifications WHERE user_id=? AND is_read=0",
                new String[]{String.valueOf(trainerId)}
        );
        if (c.moveToFirst()) {
            int count = c.getInt(0);
            if (count > 0) {
                tvNotificationBadge.setText(String.valueOf(count));
                tvNotificationBadge.setVisibility(View.VISIBLE);
            } else {
                tvNotificationBadge.setVisibility(View.GONE);
            }
        }
        c.close();
    }

    private void setupClicks() {
        // Top bar actions
        icNotification.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationActivity.class)
                        .putExtra("user_id", trainerId)));

        btnLogout.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });

        // New cards
        cardPendingRequests.setOnClickListener(v ->
                startActivity(new Intent(this, TrainerPendingRequestsActivity.class)
                        .putExtra("trainerId", trainerId)));

        cardMyTrainees.setOnClickListener(v ->
                startActivity(new Intent(this, TrainerTraineeListActivity.class)
                        .putExtra("trainerId", trainerId)));

        cardInviteTrainee.setOnClickListener(v ->
                startActivity(new Intent(this, TrainerInviteTraineeActivity.class)
                        .putExtra("trainerId", trainerId)));

        // Existing cards
        cardCreatePlan.setOnClickListener(v ->
                startActivity(new Intent(this, CreateWorkoutPlanActivity.class)
                        .putExtra("trainer_id", trainerId)));

        cardAssignWorkout.setOnClickListener(v ->
                startActivity(new Intent(this, AssignWorkoutActivity.class)
                        .putExtra("trainer_id", trainerId)));

        cardViewProgress.setOnClickListener(v ->
                startActivity(new Intent(this, TrainerProgressViewActivity.class)
                        .putExtra("trainer_id", trainerId)));

        cardSendReminder.setOnClickListener(v ->
                startActivity(new Intent(this, TrainerReminderActivity.class)
                        .putExtra("trainer_id", trainerId)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh counts when returning to dashboard
        if (trainerId != -1) {
            loadStats();
            loadPendingCount();
            loadNotificationBadge();
        }
    }
}
