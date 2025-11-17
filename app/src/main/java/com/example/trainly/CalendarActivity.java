package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
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
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView recycler;
    TextView tvSelectedDate, tvEmptyState, tvWorkoutCount;

    WorkoutHistoryAdapter adapter;
    List<WorkoutHistoryItem> dailyList = new ArrayList<>();

    DatabaseHelper db;
    int traineeId;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // FIX: Enable EdgeToEdge
        setContentView(R.layout.activity_calendar);

        // FIX: Set WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new DatabaseHelper(this);

        email = getIntent().getStringExtra("email");
        traineeId = db.getUserIdByEmail(email);

        calendarView = findViewById(R.id.calendarView);
        recycler = findViewById(R.id.recyclerCalendarWorkouts);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvEmptyState = findViewById(R.id.tvEmptyState);  // NEW
        tvWorkoutCount = findViewById(R.id.tvWorkoutCount);  // NEW

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkoutHistoryAdapter(this, new ArrayList<>(dailyList));
        recycler.setAdapter(adapter);

        // FIX: Default - load today's workouts with proper date display
        long today = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        updateDateDisplay(cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.YEAR));
        loadWorkoutsForDate(today);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth, 0, 0, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);

            long startDay = selectedCal.getTimeInMillis();
            updateDateDisplay(dayOfMonth, month, year);
            loadWorkoutsForDate(startDay);
        });
    }

    // NEW: Update date display text
    private void updateDateDisplay(int day, int month, int year) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        tvSelectedDate.setText(sdf.format(cal.getTime()));
    }

    // FIX: Add null check and empty state
    private void loadWorkoutsForDate(long startDay) {

        long endDay = startDay + 24L * 60 * 60 * 1000;

        dailyList.clear();

        Cursor c = db.getWorkoutsByDay(traineeId, startDay, endDay);

        // FIX: Add null check
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

        // FIX: Update UI based on data availability
        if (dailyList.isEmpty()) {
            showEmptyState();
        } else {
            showWorkouts();
        }

        adapter.historyList.clear();
        adapter.historyList.addAll(dailyList);
        adapter.notifyDataSetChanged();
    }

    // NEW: Show empty state
    private void showEmptyState() {
        recycler.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvWorkoutCount.setVisibility(View.GONE);
    }

    // NEW: Show workouts list
    private void showWorkouts() {
        recycler.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        tvWorkoutCount.setVisibility(View.VISIBLE);

        // Count completed vs total
        int completed = 0;
        for (WorkoutHistoryItem item : dailyList) {
            if (item.isCompleted()) {
                completed++;
            }
        }
        tvWorkoutCount.setText(completed + " of " + dailyList.size() + " workouts completed");
    }
}