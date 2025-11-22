package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
        setContentView(R.layout.activity_assign_workout);

        db = new DatabaseHelper(this);
        trainerId = getIntent().getIntExtra("trainer_id", -1);
        int preselectedTraineeId = getIntent().getIntExtra("preselected_trainee_id", -1);

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) toolbarTitle.setText(R.string.label_assign_workout);
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        spTrainee = findViewById(R.id.spTrainee);
        spPlan = findViewById(R.id.spPlan);
        btnAssign = findViewById(R.id.btnAssignWorkout);

        // Check for null views
        if (spTrainee == null || spPlan == null || btnAssign == null) {
            Toast.makeText(this, "Failed to initialize UI", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTrainees();
        loadPlans();

        // Auto-select trainee if preselected
        if (preselectedTraineeId != -1) {
            int position = traineeIds.indexOf(preselectedTraineeId);
            if (position != -1) {
                spTrainee.setSelection(position);
            }
        }

        btnAssign.setOnClickListener(v -> {
            try {
                if (traineeIds.isEmpty() || planIds.isEmpty()) {
                    Toast.makeText(this, "No trainee or plan available", Toast.LENGTH_SHORT).show();
                    return;
                }

                int traineePos = spTrainee.getSelectedItemPosition();
                int planPos = spPlan.getSelectedItemPosition();

                // Validate positions
                if (traineePos < 0 || traineePos >= traineeIds.size() ||
                        planPos < 0 || planPos >= planIds.size()) {
                    Toast.makeText(this, "Invalid selection", Toast.LENGTH_SHORT).show();
                    return;
                }

                int traineeId = traineeIds.get(traineePos);
                int planId = planIds.get(planPos);

                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(new Date());

                long res = db.assignWorkout(traineeId, planId, today);

                if (res == -1) {
                    Toast.makeText(this, "Failed to assign workout", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Workout assigned successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTrainees() {
        traineeIds.clear();
        traineeNames.clear();

        try {
            Cursor c = db.getAllClients();
            if (c != null) {
                while (c.moveToNext()) {
                    int id = c.getInt(0);
                    String name = c.getString(1);
                    String email = c.getString(2);

                    traineeIds.add(id);
                    // Nếu name rỗng thì dùng email
                    if (name == null || name.trim().isEmpty()) {
                        traineeNames.add(email != null ? email : "Unknown");
                    } else {
                        traineeNames.add(name);
                    }
                }
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Always set adapter, even if empty
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_text,
                traineeNames
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        if (spTrainee != null) {
            spTrainee.setAdapter(adapter);
        }
    }

    private void loadPlans() {
        planIds.clear();
        planTitles.clear();

        try {
            if (trainerId != -1) {
                Cursor c = db.getWorkoutPlansByTrainer(trainerId);
                if (c != null) {
                    while (c.moveToNext()) {
                        int id = c.getInt(0);
                        String title = c.getString(1);

                        planIds.add(id);
                        planTitles.add(title != null ? title : "Untitled Plan");
                    }
                    c.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Always set adapter, even if empty
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_text,
                planTitles
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        if (spPlan != null) {
            spPlan.setAdapter(adapter);
        }
    }
}
