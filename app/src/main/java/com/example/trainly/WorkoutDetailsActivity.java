package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WorkoutDetailsActivity extends AppCompatActivity {

    TextView tvTitle, tvDescription;
    Button btnViewExercises, btnStartWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
