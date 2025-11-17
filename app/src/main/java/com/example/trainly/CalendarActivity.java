package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView recycler;
    TextView tvSelectedDate, tvWorkoutCount;
    LinearLayout tvEmptyState;

    WorkoutHistoryAdapter adapter;
    List<WorkoutHistoryItem> dailyList = new ArrayList<>();

    DatabaseHelper db;
    int traineeId;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);

        email = getIntent().getStringExtra("email");

        // Check for null or invalid email
        if (email == null || email.isEmpty()) {
            finish();
            return;
        }

        traineeId = db.getUserIdByEmail(email);

        // Check for invalid traineeId
        if (traineeId <= 0) {
            finish();
            return;
        }

        calendarView = findViewById(R.id.calendarView);
        recycler = findViewById(R.id.recyclerCalendarWorkouts);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvWorkoutCount = findViewById(R.id.tvWorkoutCount);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Check for null views
        if (calendarView == null || recycler == null || tvSelectedDate == null ||
                tvWorkoutCount == null || tvEmptyState == null) {
            finish();
            return;
        }

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkoutHistoryAdapter(this, new ArrayList<>(dailyList));
        recycler.setAdapter(adapter);

        // Default: load today's workouts
        long today = System.currentTimeMillis();
        loadWorkoutsForDate(today);

        // Display today's date by default
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvSelectedDate.setText("Workouts on " + sdf.format(new Date(today)));
        } catch (Exception e) {
            tvSelectedDate.setText("Workouts on this day");
        }

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long startDay = cal.getTimeInMillis();
            loadWorkoutsForDate(startDay);

            tvSelectedDate.setText("Workouts on " + dayOfMonth + "/" + (month + 1) + "/" + year);
        });
    }

    private void loadWorkoutsForDate(long startDay) {
        try {
            long endDay = startDay + 24L * 60 * 60 * 1000;

            dailyList.clear();

            Cursor c = db.getWorkoutsByDay(traineeId, startDay, endDay);

            if (c == null) {
                showEmptyState();
                return;
            }

            while (c.moveToNext()) {
                String title = c.getString(0);
                int calories = c.getInt(1);
                String status = c.getString(2);
                long timestamp = c.getLong(3);

                dailyList.add(new WorkoutHistoryItem(
                        title,
                        calories,
                        status,
                        timestamp
                ));
            }
            c.close();

            if (dailyList.isEmpty()) {
                showEmptyState();
            } else {
                showWorkouts();
            }

            if (adapter != null) {
                adapter.historyList.clear();
                adapter.historyList.addAll(dailyList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyState();
        }
    }

    private void showEmptyState() {
        if (recycler != null) recycler.setVisibility(View.GONE);
        if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
        if (tvWorkoutCount != null) tvWorkoutCount.setVisibility(View.GONE);
    }

    private void showWorkouts() {
        if (recycler != null) recycler.setVisibility(View.VISIBLE);
        if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
        if (tvWorkoutCount != null) tvWorkoutCount.setVisibility(View.VISIBLE);

        int completed = 0;
        for (WorkoutHistoryItem item : dailyList) {
            if (item != null && item.isCompleted()) {
                completed++;
            }
        }

        if (tvWorkoutCount != null) {
            tvWorkoutCount.setText(completed + " of " + dailyList.size() + " workouts completed");
        }
    }
}