package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateWorkoutPlanActivity extends AppCompatActivity {

    EditText etPlanTitle, etPlanDescription;
    Button btnSavePlan;

    DatabaseHelper db;
    int trainerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout_plan);

        db = new DatabaseHelper(this);
        trainerId = getIntent().getIntExtra("trainer_id", -1);

        etPlanTitle = findViewById(R.id.etPlanTitle);
        etPlanDescription = findViewById(R.id.etPlanDescription);
        btnSavePlan = findViewById(R.id.btnSavePlan);

        btnSavePlan.setOnClickListener(v -> {
            String title = etPlanTitle.getText().toString().trim();
            String desc = etPlanDescription.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter plan title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (trainerId == -1) {
                Toast.makeText(this, "Trainer info not found", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = db.createWorkoutPlan(trainerId, title, desc);

            if (result == -1) {
                Toast.makeText(this, "Failed to create plan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Plan created", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
