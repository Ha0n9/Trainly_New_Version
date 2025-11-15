package com.example.trainly;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AssignWorkoutActivity extends AppCompatActivity {

    Spinner spTrainee, spPlan;
    Button btnAssign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_workout);

        spTrainee = findViewById(R.id.spTrainee);
        spPlan = findViewById(R.id.spPlan);
        btnAssign = findViewById(R.id.btnAssignWorkout);

        // Hint + data
        String[] traineeList = {"Select Trainee", "Alex", "Maria", "John"};
        String[] planList = {"Select Plan", "Push Day", "Leg Day", "Pull Day"};

        // Trainee Adapter
        ArrayAdapter<String> traineeAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_text,
                traineeList
        );
        traineeAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spTrainee.setAdapter(traineeAdapter);

        // Plan Adapter
        ArrayAdapter<String> planAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_text,
                planList
        );
        planAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spPlan.setAdapter(planAdapter);

        btnAssign.setOnClickListener(v -> finish());
    }
}
