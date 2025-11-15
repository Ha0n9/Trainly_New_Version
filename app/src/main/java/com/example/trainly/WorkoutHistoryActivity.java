package com.example.trainly;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerHistory;
    ArrayList<WorkoutHistoryItem> historyList;
    WorkoutHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerHistory = findViewById(R.id.recyclerHistory);

        historyList = new ArrayList<>();
        historyList.add(new WorkoutHistoryItem("Jan 4, 2025", "Push Day", "4/4 completed"));
        historyList.add(new WorkoutHistoryItem("Jan 3, 2025", "Leg Day", "3/5 completed"));
        historyList.add(new WorkoutHistoryItem("Jan 2, 2025", "Pull Day", "5/5 completed"));

        adapter = new WorkoutHistoryAdapter(historyList);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setAdapter(adapter);
    }
}