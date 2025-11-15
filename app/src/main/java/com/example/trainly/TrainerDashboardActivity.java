package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrainerDashboardActivity extends AppCompatActivity {

    CardView cardCreatePlan, cardAssignWorkout, cardViewProgress, cardSendReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardCreatePlan = findViewById(R.id.cardCreatePlan);
        cardAssignWorkout = findViewById(R.id.cardAssignWorkout);
        cardViewProgress = findViewById(R.id.cardViewProgress);
        cardSendReminder = findViewById(R.id.cardSendReminder);

        cardCreatePlan.setOnClickListener(v ->
                startActivity(new Intent(this, CreateWorkoutPlanActivity.class)));

        cardAssignWorkout.setOnClickListener(v ->
                startActivity(new Intent(this, AssignWorkoutActivity.class)));

        cardViewProgress.setOnClickListener(v ->
                startActivity(new Intent(this, TrainerProgressViewActivity.class)));

        cardSendReminder.setOnClickListener(v ->
                startActivity(new Intent(this, TrainerReminderActivity.class)));
    }
}