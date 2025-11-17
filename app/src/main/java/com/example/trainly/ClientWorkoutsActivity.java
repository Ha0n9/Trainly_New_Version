package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClientWorkoutsActivity extends AppCompatActivity {

    RecyclerView recyclerClientWorkouts;
    ArrayList<Workout> workoutList;
    WorkoutAdapter workoutAdapter;

    DatabaseHelper db;
    String email;
    int traineeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_workouts);

        db = new DatabaseHelper(this);

        recyclerClientWorkouts = findViewById(R.id.recyclerClientWorkouts);
        workoutList = new ArrayList<>();

        // Get email from Intent
        email = getIntent().getStringExtra("email");

        if (email == null) {
            finish();
            return;
        }

        // Get trainee ID
        traineeId = db.getUserIdByEmail(email);

        // Load workouts from DB
        loadAssignedWorkouts(traineeId);

        // Set adapter with click listener
        workoutAdapter = new WorkoutAdapter(workoutList, workout -> {
            // Start workout when clicked
            Intent intent = new Intent(ClientWorkoutsActivity.this, StartWorkoutActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("assignedWorkoutId", workout.getAssignedWorkoutId());
            intent.putExtra("planId", workout.getPlanId());
            intent.putExtra("workoutTitle", workout.getTitle());
            startActivity(intent);
        });

        recyclerClientWorkouts.setLayoutManager(new LinearLayoutManager(this));
        recyclerClientWorkouts.setAdapter(workoutAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload workouts when returning from StartWorkoutActivity
        workoutList.clear();
        loadAssignedWorkouts(traineeId);
        workoutAdapter.notifyDataSetChanged();
    }

    private void loadAssignedWorkouts(int traineeId) {
        Cursor c = db.getAssignedWorkouts(traineeId);

        if (c == null) return;

        while (c.moveToNext()) {
            int assignedWorkoutId = c.getInt(0);
            String title = c.getString(1);
            String description = c.getString(2);
            int completed = c.getInt(3);
            int planId = c.getInt(4);

            workoutList.add(new Workout(
                    title,
                    description,
                    completed == 1,
                    assignedWorkoutId,
                    planId
            ));
        }

        c.close();
    }
}