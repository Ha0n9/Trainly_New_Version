package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CreateWorkoutPlanActivity extends AppCompatActivity {

    EditText etPlanTitle, etPlanDescription;
    Button btnSavePlan;

    DatabaseHelper db;
    int trainerId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_workout_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
