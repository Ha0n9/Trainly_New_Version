package com.example.trainly;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TrainerDashboardActivity extends AppCompatActivity {

    TextView tvTrainerGreeting, tvStatClients, tvStatPlans, tvStatAssigned;
    CardView cardCreatePlan, cardAssignWorkout, cardViewProgress, cardSendReminder;

    DatabaseHelper db;
    String email;
    int trainerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer_dashboard);

        db = new DatabaseHelper(this);

        // GET EMAIL FROM LOGIN
        email = getIntent().getStringExtra("email");

        initViews();
        loadTrainerInfo();
        loadStats();
        setupClicks();
    }

    private void initViews() {
        tvTrainerGreeting = findViewById(R.id.tvTrainerGreeting);
        tvStatClients = findViewById(R.id.tvStatClients);
        tvStatPlans = findViewById(R.id.tvStatPlans);
        tvStatAssigned = findViewById(R.id.tvStatAssigned);

        cardCreatePlan = findViewById(R.id.cardCreatePlan);
        cardAssignWorkout = findViewById(R.id.cardAssignWorkout);
        cardViewProgress = findViewById(R.id.cardViewProgress);
        cardSendReminder = findViewById(R.id.cardSendReminder);
    }

    private void loadTrainerInfo() {
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT id, name FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            trainerId = c.getInt(0);
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

    private void setupClicks() {
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
}
