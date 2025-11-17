package com.example.trainly;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TodayWorkoutActivity extends AppCompatActivity {

    DatabaseHelper db;
    String email;
    int traineeId;

    CardView cardWorkout1, cardWorkout2, cardWorkout3;
    TextView tvWorkout1Title, tvWorkout1Desc, tvWorkout1Exercises,
            tvWorkout2Title, tvWorkout2Desc, tvWorkout2Exercises,
            tvWorkout3Title, tvWorkout3Desc, tvWorkout3Exercises;
    TextView tvNoWorkouts;

    ArrayList<WorkoutItem> workoutItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_today_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);

        // Get email from intent
        email = getIntent().getStringExtra("email");
        if (email == null) {
            finish();
            return;
        }

        traineeId = db.getUserIdByEmail(email);

        // Bind views
        cardWorkout1 = findViewById(R.id.cardWorkout1);
        cardWorkout2 = findViewById(R.id.cardWorkout2);
        cardWorkout3 = findViewById(R.id.cardWorkout3);

        tvWorkout1Title = findViewById(R.id.tvWorkout1Title);
        tvWorkout1Desc = findViewById(R.id.tvWorkout1Desc);
        tvWorkout1Exercises = findViewById(R.id.tvWorkout1Exercises);

        tvWorkout2Title = findViewById(R.id.tvWorkout2Title);
        tvWorkout2Desc = findViewById(R.id.tvWorkout2Desc);
        tvWorkout2Exercises = findViewById(R.id.tvWorkout2Exercises);

        tvWorkout3Title = findViewById(R.id.tvWorkout3Title);
        tvWorkout3Desc = findViewById(R.id.tvWorkout3Desc);
        tvWorkout3Exercises = findViewById(R.id.tvWorkout3Exercises);

        tvNoWorkouts = findViewById(R.id.tvNoWorkouts);

        loadTodayWorkouts();
    }

    private void loadTodayWorkouts() {
        Cursor c = db.getTodayWorkouts(traineeId);

        if (c == null || c.getCount() == 0) {
            // No workouts today
            tvNoWorkouts.setVisibility(View.VISIBLE);
            cardWorkout1.setVisibility(View.GONE);
            cardWorkout2.setVisibility(View.GONE);
            cardWorkout3.setVisibility(View.GONE);
            if (c != null) c.close();
            return;
        }

        tvNoWorkouts.setVisibility(View.GONE);

        int index = 0;
        while (c.moveToNext() && index < 3) {
            int assignedWorkoutId = c.getInt(0);
            int planId = c.getInt(1);
            String title = c.getString(2);
            String description = c.getString(3);
            int exerciseCount = c.getInt(5);

            workoutItems.add(new WorkoutItem(assignedWorkoutId, planId, title, description, exerciseCount));
            index++;
        }
        c.close();

        // Show cards based on count
        if (workoutItems.size() >= 1) {
            setupWorkoutCard(cardWorkout1, tvWorkout1Title, tvWorkout1Desc, tvWorkout1Exercises, workoutItems.get(0), 0);
        } else {
            cardWorkout1.setVisibility(View.GONE);
        }

        if (workoutItems.size() >= 2) {
            setupWorkoutCard(cardWorkout2, tvWorkout2Title, tvWorkout2Desc, tvWorkout2Exercises, workoutItems.get(1), 1);
        } else {
            cardWorkout2.setVisibility(View.GONE);
        }

        if (workoutItems.size() >= 3) {
            setupWorkoutCard(cardWorkout3, tvWorkout3Title, tvWorkout3Desc, tvWorkout3Exercises, workoutItems.get(2), 2);
        } else {
            cardWorkout3.setVisibility(View.GONE);
        }
    }

    private void setupWorkoutCard(CardView card, TextView tvTitle, TextView tvDesc, TextView tvExercises, WorkoutItem item, int position) {
        card.setVisibility(View.VISIBLE);
        tvTitle.setText(item.title);
        tvDesc.setText(item.description);
        tvExercises.setText(item.exerciseCount + " exercises");

        card.setOnClickListener(v -> {
            Intent intent = new Intent(TodayWorkoutActivity.this, StartWorkoutActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("assignedWorkoutId", item.assignedWorkoutId);
            intent.putExtra("planId", item.planId);
            intent.putExtra("workoutTitle", item.title);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload when coming back
        workoutItems.clear();
        loadTodayWorkouts();
    }

    // Inner class for workout items
    private static class WorkoutItem {
        int assignedWorkoutId;
        int planId;
        String title;
        String description;
        int exerciseCount;

        WorkoutItem(int assignedWorkoutId, int planId, String title, String description, int exerciseCount) {
            this.assignedWorkoutId = assignedWorkoutId;
            this.planId = planId;
            this.title = title;
            this.description = description;
            this.exerciseCount = exerciseCount;
        }
    }
}