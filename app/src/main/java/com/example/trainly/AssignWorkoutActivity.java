package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AssignWorkoutActivity extends AppCompatActivity {

    Spinner spTrainee, spPlan;
    Button btnAssign;

    DatabaseHelper db;
    int trainerId = -1;

    // Để nhớ id thật trong DB
    ArrayList<Integer> traineeIds = new ArrayList<>();
    ArrayList<String> traineeNames = new ArrayList<>();

    ArrayList<Integer> planIds = new ArrayList<>();
    ArrayList<String> planTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_assign_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);
        trainerId = getIntent().getIntExtra("trainer_id", -1);

        spTrainee = findViewById(R.id.spTrainee);
        spPlan = findViewById(R.id.spPlan);
        btnAssign = findViewById(R.id.btnAssignWorkout);

        loadTrainees();
        loadPlans();

        btnAssign.setOnClickListener(v -> {
            if (traineeIds.isEmpty() || planIds.isEmpty()) {
                Toast.makeText(this, "No trainee or plan available", Toast.LENGTH_SHORT).show();
                return;
            }

            int traineePos = spTrainee.getSelectedItemPosition();
            int planPos = spPlan.getSelectedItemPosition();

            int traineeId = traineeIds.get(traineePos);
            int planId = planIds.get(planPos);

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            long res = db.assignWorkout(traineeId, planId, today);

            if (res == -1) {
                Toast.makeText(this, "Failed to assign workout", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Workout assigned", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTrainees() {
        traineeIds.clear();
        traineeNames.clear();

        Cursor c = db.getAllClients();
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String name = c.getString(1);
                String email = c.getString(2);

                traineeIds.add(id);
                // Nếu name rỗng thì dùng email
                if (name == null || name.trim().isEmpty()) {
                    traineeNames.add(email);
                } else {
                    traineeNames.add(name);
                }
            }
            c.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                traineeNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrainee.setAdapter(adapter);
    }

    private void loadPlans() {
        planIds.clear();
        planTitles.clear();

        if (trainerId == -1) {
            return;
        }

        Cursor c = db.getWorkoutPlansByTrainer(trainerId);
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String title = c.getString(1);

                planIds.add(id);
                planTitles.add(title);
            }
            c.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                planTitles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlan.setAdapter(adapter);
    }
}
