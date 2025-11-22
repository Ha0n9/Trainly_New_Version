package com.example.trainly;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StartWorkoutActivity extends AppCompatActivity {

    RecyclerView recyclerStartWorkout;
    ArrayList<Exercise> exerciseList;
    StartWorkoutAdapter adapter;
    TextView tvStartWorkoutTitle;
    Button btnCompleteWorkout;

    DatabaseHelper db;
    String email;
    int traineeId;
    int assignedWorkoutId;
    int planId;
    String workoutTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        db = new DatabaseHelper(this);

        recyclerStartWorkout = findViewById(R.id.recyclerStartWorkout);
        tvStartWorkoutTitle = findViewById(R.id.tvStartWorkoutTitle);
        btnCompleteWorkout = findViewById(R.id.btnCompleteWorkout);

        // Get data from Intent
        email = getIntent().getStringExtra("email");
        assignedWorkoutId = getIntent().getIntExtra("assignedWorkoutId", -1);
        planId = getIntent().getIntExtra("planId", -1);
        workoutTitle = getIntent().getStringExtra("workoutTitle");

        if (email == null || assignedWorkoutId == -1 || planId == -1) {
            Toast.makeText(this, "Invalid workout data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        traineeId = db.getUserIdByEmail(email);

        // Set title
        if (workoutTitle != null) {
            tvStartWorkoutTitle.setText(workoutTitle);
        }

        // Load exercises from database
        exerciseList = new ArrayList<>();
        loadExercises(planId);

        adapter = new StartWorkoutAdapter(exerciseList);
        recyclerStartWorkout.setLayoutManager(new LinearLayoutManager(this));
        recyclerStartWorkout.setAdapter(adapter);

        // Complete workout button
        btnCompleteWorkout.setOnClickListener(v -> showCompleteDialog());
    }

    private void loadExercises(int planId) {
        Cursor c = db.getExercisesByPlanId(planId);

        if (c == null || c.getCount() == 0) {
            // No exercises found - show placeholder
            exerciseList.add(new Exercise("No exercises assigned yet", "Check with your trainer"));
            if (c != null) c.close();
            return;
        }

        while (c.moveToNext()) {
            String name = c.getString(1);
            int sets = c.getInt(2);
            int reps = c.getInt(3);
            String notes = c.getString(4);

            String details = sets + " sets × " + reps + " reps";
            if (notes != null && !notes.isEmpty()) {
                details += " - " + notes;
            }

            exerciseList.add(new Exercise(name, details));
        }

        c.close();
    }

    private void showCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_complete_workout, null);

        EditText etCalories = dialogView.findViewById(R.id.etCaloriesInput);

        builder.setView(dialogView)
                .setTitle("Complete Workout")
                .setMessage("How many calories did you burn?")
                .setPositiveButton("Complete", (dialog, which) -> {
                    String caloriesStr = etCalories.getText().toString().trim();

                    if (caloriesStr.isEmpty()) {
                        Toast.makeText(this, "Please enter calories", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int calories = Integer.parseInt(caloriesStr);

                    // Mark as completed in assigned_workouts
                    boolean updated = db.markWorkoutAsCompleted(assignedWorkoutId);

                    // Insert into workout_history
                    db.insertWorkoutHistory(traineeId, planId, calories, "completed");

                    if (updated) {
                        Toast.makeText(this, "Workout completed! Great job!", Toast.LENGTH_SHORT).show();
                        finish(); // Return to ClientWorkoutsActivity
                    } else {
                        Toast.makeText(this, "Error completing workout", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}