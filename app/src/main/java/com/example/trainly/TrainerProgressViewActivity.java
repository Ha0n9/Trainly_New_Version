package com.example.trainly;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrainerProgressViewActivity extends AppCompatActivity {

    LinearLayout progressList;

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

        progressList = findViewById(R.id.progressList);

        // fake trainee progress
        addProgressRow("Alex", "72%");
        addProgressRow("Maria", "55%");
        addProgressRow("John", "90%");
    }

    private void addProgressRow(String name, String percent) {
        TextView tv = new TextView(this);
        tv.setText(name + " — " + percent);
        tv.setPadding(10, 12, 10, 12);
        tv.setTextColor(getResources().getColor(R.color.text_primary));
        tv.setTextSize(18);
        progressList.addView(tv);
    }
}