package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkoutDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDescription;
    Button btnViewExercises, btnStartWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        // Bind views
        tvTitle = findViewById(R.id.tvWorkoutTitle);
        tvDescription = findViewById(R.id.tvWorkoutDescription);
        btnViewExercises = findViewById(R.id.btnViewExercises);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);

        // Fake Data for UI
        tvTitle.setText("Push Day");
        tvDescription.setText("Chest + Shoulders + Triceps");

        btnViewExercises.setOnClickListener(v ->
                startActivity(new Intent(this, ExerciseListActivity.class))
        );

        btnStartWorkout.setOnClickListener(v ->
                startActivity(new Intent(this, ExerciseListActivity.class))
        );
    }
}
