package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkoutHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerHistory;
    ArrayList<WorkoutHistoryItem> historyList;
    WorkoutHistoryAdapter adapter;
    DatabaseHelper db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_history);

        db = new DatabaseHelper(this);

        recyclerHistory = findViewById(R.id.recyclerHistory);
        historyList = new ArrayList<>();

        // Get email from intent
        email = getIntent().getStringExtra("email");
        if (email == null) {
            finish();
            return;
        }

        int traineeId = db.getUserIdByEmail(email);

        loadWorkoutHistory(traineeId);

        adapter = new WorkoutHistoryAdapter(this, historyList);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setAdapter(adapter);
    }

    private void loadWorkoutHistory(int traineeId) {
        Cursor c = db.getWeeklyHistory(traineeId);

        if (c == null) return;

        while (c.moveToNext()) {

            long timestamp = Long.parseLong(c.getString(0));
            String dateStr = formatDate(timestamp);

            int calories = c.getInt(1);
            String status = c.getString(2);

            boolean completed = "completed".equals(status);

            String summary = calories + " kcal";

            historyList.add(new WorkoutHistoryItem(
                    dateStr,
                    completed ? "Completed Workout" : "Workout Attempt",
                    summary,
                    completed
            ));
        }

        c.close();
    }

    private String formatDate(long millis) {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(new java.util.Date(millis));
    }
}
