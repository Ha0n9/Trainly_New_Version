package com.example.trainly;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ClientProgressActivity extends AppCompatActivity {

    TextView tvProgressPercent, tvWorkoutsDone, tvCalories, tvWeeklyGoal, tvStreak;

    DatabaseHelper db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_progress);

        db = new DatabaseHelper(this);

        // Bind views
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvWorkoutsDone = findViewById(R.id.tvWorkoutsDone);
        tvCalories = findViewById(R.id.tvCalories);
        tvWeeklyGoal = findViewById(R.id.tvWeeklyGoal);
        tvStreak = findViewById(R.id.tvStreak);

        // Get email from Intent (ClientProfileActivity phải putExtra("email", ...))
        email = getIntent().getStringExtra("email");

        if (email == null) {
            // Không có email thì không biết user nào -> đóng màn
            finish();
            return;
        }

        int traineeId = db.getUserIdByEmail(email);

        // ===== BASIC STATS =====
        int totalAssigned = db.getAssignedWorkoutsCount(traineeId);
        int completed = db.getCompletedWorkouts(traineeId);
        int calories = db.getTotalCalories(traineeId);

        // Avoid divide by zero
        int percent = 0;
        if (totalAssigned > 0) {
            percent = (completed * 100) / totalAssigned;
        }

        // Update basic UI
        tvProgressPercent.setText(percent + "%");
        tvWorkoutsDone.setText(completed + " / " + totalAssigned);
        tvCalories.setText(calories + " kcal");

        // ===== WEEKLY STATS (7 ngày gần nhất) =====
        int totalWeekly = 0;
        int completedWeekly = 0;

        Cursor w = db.getWeeklyHistory(traineeId);
        while (w.moveToNext()) {
            String status = w.getString(2); // date, calories, status
            if ("completed".equals(status)) completedWeekly++;
            totalWeekly++;
        }
        w.close();

        int weeklyGoal = 4; // hard-code goal 4 buổi/tuần
        int weeklyPercent = 0;
        if (weeklyGoal > 0) {
            weeklyPercent = (completedWeekly * 100) / weeklyGoal;
        }

        // Update weekly UI label
        // Ví dụ: "Weekly goal: 4 workouts • This week: 2/4 (50%)"
        String weeklyText = "Weekly goal: " + weeklyGoal +
                " workouts • This week: " + completedWeekly + "/" + weeklyGoal +
                " (" + weeklyPercent + "%)";
        tvWeeklyGoal.setText(weeklyText);

        // ===== STREAK (chuỗi ngày tập liên tục) =====
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
     * Tính streak: số ngày tập liên tục tính từ hôm nay lùi về, dựa trên
     * các bản ghi workout_history có status = 'completed'.
     * date trong DB hiện đang lưu millis (String).
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
        long oneDay = 24L * 60 * 60 * 1000; // 1 ngày (ms)
        int streak = 0;

        while (c.moveToNext()) {
            long workoutDay;
            try {
                workoutDay = Long.parseLong(c.getString(0));
            } catch (NumberFormatException e) {
                // Nếu vì lý do gì đó date không phải millis -> bỏ qua
                continue;
            }

            // Nếu lần workout này nằm trong khoảng (streak+1) ngày trở lại
            // ví dụ streak=0 -> trong 1 ngày; streak=1 -> trong 2 ngày; v.v.
            if (today - workoutDay <= oneDay * (streak + 1)) {
                streak++;
            } else {
                break;
            }
        }

        c.close();
        return streak;
    }

}
