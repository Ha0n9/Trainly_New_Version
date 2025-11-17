package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerProgressViewActivity extends AppCompatActivity {

    RecyclerView recyclerProgress;
    LinearLayout tvEmpty;  // FIXED: Đổi từ TextView sang LinearLayout
    TextView tvTotalStats;
    DatabaseHelper db;
    int trainerId = -1;

    ArrayList<TraineeProgressItem> progressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer_progress_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);
        trainerId = getIntent().getIntExtra("trainer_id", -1);

        recyclerProgress = findViewById(R.id.recyclerProgress);
        tvEmpty = findViewById(R.id.tvEmptyProgress);
        tvTotalStats = findViewById(R.id.tvTotalStats);

        recyclerProgress.setLayoutManager(new LinearLayoutManager(this));

        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadProgress();
    }

    private void loadProgress() {
        progressList.clear();

        if (trainerId == -1) {
            showEmptyState();
            return;
        }

        Cursor c = db.getTraineeProgressForTrainer(trainerId);

        if (c == null || c.getCount() == 0) {
            showEmptyState();
            if (c != null) c.close();
            return;
        }

        int totalTrainees = 0;
        int totalAssigned = 0;
        int totalCompleted = 0;

        while (c.moveToNext()) {
            int traineeId = c.getInt(0);
            String name = c.getString(1);
            int assigned = c.getInt(2);
            int completed = c.getInt(3);

            progressList.add(new TraineeProgressItem(traineeId, name, assigned, completed));

            totalTrainees++;
            totalAssigned += assigned;
            totalCompleted += completed;
        }
        c.close();

        // Show stats
        showProgress();

        // Update total stats
        String statsText = "Total: " + totalTrainees + " trainees • " +
                totalCompleted + "/" + totalAssigned + " workouts completed";
        tvTotalStats.setText(statsText);

        // Set adapter
        TrainerProgressAdapter adapter = new TrainerProgressAdapter(this, progressList, trainerId);
        recyclerProgress.setAdapter(adapter);
    }

    private void showEmptyState() {
        recyclerProgress.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        tvTotalStats.setVisibility(View.GONE);
    }

    private void showProgress() {
        recyclerProgress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        tvTotalStats.setVisibility(View.VISIBLE);
    }
}