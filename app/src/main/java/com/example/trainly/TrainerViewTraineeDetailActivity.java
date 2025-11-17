package com.example.trainly;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerViewTraineeDetailActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvAge, tvHeight, tvWeight, tvBMI;
    TextView tvTotalWorkouts, tvCompleted, tvFailed, tvTotalCalories;
    TextView tvEmptyHistory;
    Button btnAssignWorkout, btnSendReminder, btnRemoveTrainee;
    ImageView btnBack;
    RecyclerView recyclerHistory;

    DatabaseHelper db;
    int trainerId, traineeId;
    String traineeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_view_trainee_detail);

        db = new DatabaseHelper(this);

        trainerId = getIntent().getIntExtra("trainerId", -1);
        traineeId = getIntent().getIntExtra("traineeId", -1);

        if (trainerId == -1 || traineeId == -1) {
            Toast.makeText(this, "Error loading trainee", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadTraineeInfo();
        loadWorkoutStats();
        loadWorkoutHistory();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvTraineeName);
        tvEmail = findViewById(R.id.tvTraineeEmail);
        tvAge = findViewById(R.id.tvTraineeAge);
        tvHeight = findViewById(R.id.tvTraineeHeight);
        tvWeight = findViewById(R.id.tvTraineeWeight);
        tvBMI = findViewById(R.id.tvTraineeBMI);

        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts);
        tvCompleted = findViewById(R.id.tvCompleted);
        tvFailed = findViewById(R.id.tvFailed);
        tvTotalCalories = findViewById(R.id.tvTotalCalories);

        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
        recyclerHistory = findViewById(R.id.recyclerWorkoutHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        btnAssignWorkout = findViewById(R.id.btnAssignWorkout);
        btnSendReminder = findViewById(R.id.btnSendReminder);
        btnRemoveTrainee = findViewById(R.id.btnRemoveTrainee);
    }

    private void loadTraineeInfo() {
        Cursor c = db.getTraineeDetailById(traineeId);

        if (c.moveToFirst()) {
            traineeName = c.getString(1);
            String email = c.getString(2);
            int age = c.getInt(3);
            double height = c.getDouble(4);
            double weight = c.getDouble(5);

            tvName.setText(traineeName);
            tvEmail.setText(email);
            tvAge.setText(age + " years old");
            tvHeight.setText(String.format("%.1f cm", height));
            tvWeight.setText(String.format("%.1f kg", weight));

            // Calculate BMI
            double bmi = weight / ((height / 100) * (height / 100));
            tvBMI.setText(String.format("%.1f", bmi));
        }
        c.close();
    }

    private void loadWorkoutStats() {
        Cursor c = db.getTraineeWorkoutStats(traineeId);

        if (c.moveToFirst()) {
            int total = c.getInt(0);
            int completed = c.getInt(1);
            int failed = c.getInt(2);
            int calories = c.getInt(3);

            tvTotalWorkouts.setText(String.valueOf(total));
            tvCompleted.setText(String.valueOf(completed));
            tvFailed.setText(String.valueOf(failed));
            tvTotalCalories.setText(String.valueOf(calories));
        }
        c.close();
    }

    private void loadWorkoutHistory() {
        ArrayList<WorkoutHistoryItem> list = new ArrayList<>();

        Cursor c = db.getWeeklyHistory(traineeId);

        while (c.moveToNext()) {
            long date = c.getLong(0);
            int calories = c.getInt(1);
            String status = c.getString(2);

            list.add(new WorkoutHistoryItem("Workout", date, calories, status));
        }
        c.close();

        if (list.isEmpty()) {
            recyclerHistory.setVisibility(View.GONE);
            tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            recyclerHistory.setVisibility(View.VISIBLE);
            tvEmptyHistory.setVisibility(View.GONE);

            WorkoutHistoryAdapter adapter = new WorkoutHistoryAdapter(this, list);
            recyclerHistory.setAdapter(adapter);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAssignWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AssignWorkoutActivity.class);
            intent.putExtra("trainer_id", trainerId);
            intent.putExtra("preselected_trainee_id", traineeId);
            startActivity(intent);
        });

        btnSendReminder.setOnClickListener(v -> {
            db.insertNotification(traineeId, "Reminder from your trainer: Keep up the good work!");
            Toast.makeText(this, "Reminder sent to " + traineeName, Toast.LENGTH_SHORT).show();
        });

        btnRemoveTrainee.setOnClickListener(v -> showRemoveDialog());
    }

    private void showRemoveDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Remove Trainee")
                .setMessage("Are you sure you want to remove " + traineeName + " from your client list?")
                .setPositiveButton("Yes, Remove", (dialog, which) -> {
                    boolean success = db.removeTrainee(trainerId, traineeId);

                    if (success) {
                        Toast.makeText(this, traineeName + " has been removed", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to trainee list
                    } else {
                        Toast.makeText(this, "Failed to remove trainee", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}