package com.example.trainly;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class ClientProgressActivity extends AppCompatActivity {

    TextView tvProgressPercent, tvWorkoutsDone, tvCalories, tvWeeklyGoal, tvStreak;

    DatabaseHelper db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_progress);

        db = new DatabaseHelper(this);

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText(getString(R.string.progress_title));
        }
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Bind views
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvWorkoutsDone = findViewById(R.id.tvWorkoutsDone);
        tvCalories = findViewById(R.id.tvCalories);
        tvWeeklyGoal = findViewById(R.id.tvWeeklyGoal);
        tvStreak = findViewById(R.id.tvStreak);

        // Get email from Intent
        email = getIntent().getStringExtra("email");

        if (email == null) {
            finish();
            return;
        }

        int traineeId = db.getUserIdByEmail(email);

        // ===== BASIC STATS (FIX: Use same table for consistency) =====
        int totalAssigned = db.getAssignedWorkoutsCount(traineeId);
        int completedAssigned = getCompletedAssignedWorkouts(traineeId);
        int calories = db.getTotalCalories(traineeId);

        // Avoid divide by zero
        int percent = 0;
        if (totalAssigned > 0) {
            percent = (completedAssigned * 100) / totalAssigned;
        }

        // Update basic UI
        tvProgressPercent.setText(percent + "%");
        tvWorkoutsDone.setText(completedAssigned + " / " + totalAssigned);
        tvCalories.setText(calories + " kcal");

        // ===== WEEKLY STATS (7 ngày gần nhất) =====
        int completedWeekly = 0;

        Cursor w = db.getWeeklyHistory(traineeId);
        while (w.moveToNext()) {
            String status = w.getString(2); // date, calories, status
            if ("completed".equals(status)) completedWeekly++;
        }
        w.close();

        int weeklyGoal = 4; // hard-code goal 4 buổi/tuần
        int weeklyPercent = 0;
        if (weeklyGoal > 0) {
            weeklyPercent = (completedWeekly * 100) / weeklyGoal;
        }

        // Update weekly UI label
        String weeklyText = "Weekly goal: " + weeklyGoal +
                " workouts • This week: " + completedWeekly + "/" + weeklyGoal +
                " (" + weeklyPercent + "%)";
        tvWeeklyGoal.setText(weeklyText);

        // ===== STREAK (FIX: Count days, not workouts) =====
        int streak = getStreak(traineeId);
        if (streak == 0) {
            tvStreak.setText("No streak yet");
        } else if (streak == 1) {
            tvStreak.setText("1 day");
        } else {
            tvStreak.setText(streak + " days");
        }
    }

    /**
     * FIX: Get completed workouts from assigned_workouts table (is_completed=1)
     * instead of workout_history to ensure consistency
     */
    private int getCompletedAssignedWorkouts(int traineeId) {
        SQLiteDatabase sqldb = db.getReadableDatabase();
        Cursor c = sqldb.rawQuery(
                "SELECT COUNT(*) FROM assigned_workouts WHERE trainee_id=? AND is_completed=1",
                new String[]{String.valueOf(traineeId)}
        );

        int count = 0;
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        c.close();
        return count;
    }

    /**
     * FIX: Count consecutive DAYS with workouts, not total workouts
     * Use HashSet to track unique days
     */
    private int getStreak(int traineeId) {
        SQLiteDatabase sqldb = db.getReadableDatabase();

        Cursor c = sqldb.rawQuery(
                "SELECT date FROM workout_history " +
                        "WHERE trainee_id=? AND status='completed' " +
                        "ORDER BY date DESC",
                new String[]{String.valueOf(traineeId)}
        );

        long today = System.currentTimeMillis();
        long oneDay = 24L * 60 * 60 * 1000;

        // Track unique days
        Set<Integer> uniqueDays = new HashSet<>();

        while (c.moveToNext()) {
            long workoutDay;
            try {
                workoutDay = Long.parseLong(c.getString(0));
            } catch (NumberFormatException e) {
                continue;
            }

            // Calculate how many days ago this workout was
            int daysAgo = (int) ((today - workoutDay) / oneDay);

            // Only consider workouts within reasonable range
            if (daysAgo >= 0 && daysAgo < 365) {
                uniqueDays.add(daysAgo);
            }
        }
        c.close();

        // Count consecutive days starting from today or yesterday
        int streak = 0;

        // Check if there's a workout today or yesterday (grace period)
        if (uniqueDays.contains(0) || uniqueDays.contains(1)) {
            // Start counting from day 0
            for (int day = 0; day < 365; day++) {
                if (uniqueDays.contains(day)) {
                    streak++;
                } else {
                    // Allow 1-day skip (rest day)
                    if (day > 0 && uniqueDays.contains(day + 1)) {
                        continue; // Skip rest day
                    } else {
                        break; // Streak broken
                    }
                }
            }
        }

        return streak;
    }

}
