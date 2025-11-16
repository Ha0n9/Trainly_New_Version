package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrainerProgressViewActivity extends AppCompatActivity {

    LinearLayout layoutTraineeProgress;
    DatabaseHelper db;
    int trainerId = -1;

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

        layoutTraineeProgress = findViewById(R.id.progressList);

        loadProgress();
    }

    private void loadProgress() {
        layoutTraineeProgress.removeAllViews();

        if (trainerId == -1) return;

        Cursor c = db.getTraineeProgressForTrainer(trainerId);
        if (c != null) {
            while (c.moveToNext()) {
                String name = c.getString(1);
                int totalAssigned = c.getInt(2);
                int completed = c.getInt(3);

                TextView tv = new TextView(this);
                tv.setText(name + " • " + completed + " / " + totalAssigned + " workouts completed");
                tv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                tv.setTextSize(16f);
                int pad = (int) (8 * getResources().getDisplayMetrics().density);
                tv.setPadding(0, pad, 0, pad);

                layoutTraineeProgress.addView(tv);
            }
            c.close();
        }
    }
}
