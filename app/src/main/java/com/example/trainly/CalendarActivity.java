package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    RecyclerView recycler;
    TextView tvSelectedDate;

    WorkoutHistoryAdapter adapter;
    List<WorkoutHistoryItem> dailyList = new ArrayList<>();

    DatabaseHelper db;
    int traineeId;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        db = new DatabaseHelper(this);

        email = getIntent().getStringExtra("email");
        traineeId = db.getUserIdByEmail(email);

        calendarView = findViewById(R.id.calendarView);
        recycler = findViewById(R.id.recyclerCalendarWorkouts);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WorkoutHistoryAdapter(this, new ArrayList<>(dailyList));
        recycler.setAdapter(adapter);

        // Default: load today's workouts
        long today = System.currentTimeMillis();
        loadWorkoutsForDate(today);

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

        long endDay = startDay + 24L * 60 * 60 * 1000;

        dailyList.clear();

        Cursor c = db.getWorkoutsByDay(traineeId, startDay, endDay);

        while (c.moveToNext()) {
            String title = c.getString(0);
            int calories = c.getInt(1);
            String status = c.getString(2);
            long timestamp = c.getLong(3);

            // dùng constructor mới
            dailyList.add(new WorkoutHistoryItem(
                    title,
                    calories,
                    status,
                    timestamp
            ));
        }
        c.close();

        adapter.historyList.clear();
        adapter.historyList.addAll(dailyList);
        adapter.notifyDataSetChanged();

    }
}
