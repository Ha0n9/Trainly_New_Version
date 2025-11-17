package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ClientProfileActivity extends AppCompatActivity {

    TextView tvUserFullname, tvUserRole;
    TextView tvStatWorkouts, tvStatCalories, tvStatWeight;

    CardView cardWorkouts, cardProgress, cardMeals, cardEditProfile, cardBMICalculator,
            cardWorkoutCalendar, cardChooseTrainer, cardNotification;

    int traineeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        // bind UI
        tvUserFullname = findViewById(R.id.tvUserFullname);
        tvUserRole = findViewById(R.id.tvUserRole);

        tvStatWorkouts = findViewById(R.id.tvStatWorkouts);
        tvStatCalories = findViewById(R.id.tvStatCalories);
        tvStatWeight = findViewById(R.id.tvStatWeight);

        cardWorkouts = findViewById(R.id.cardWorkouts);
        cardProgress = findViewById(R.id.cardProgress);
        cardMeals = findViewById(R.id.cardMeals);
        cardEditProfile = findViewById(R.id.cardEditProfile);
        cardBMICalculator = findViewById(R.id.cardBMICalculator);
        cardWorkoutCalendar = findViewById(R.id.cardWorkoutCalendar);
        cardChooseTrainer = findViewById(R.id.cardChooseTrainer);
        cardNotification = findViewById(R.id.cardNotificaton);

        // get user info
        String name = getIntent().getStringExtra("name");
        String role = getIntent().getStringExtra("role");
        String email = getIntent().getStringExtra("email");

        if (name != null) tvUserFullname.setText(name);
        if (role != null) tvUserRole.setText(role);
        if (email == null) {
            Toast.makeText(this, "Error: missing email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ===== LOAD STATS =====
        DatabaseHelper db = new DatabaseHelper(this);
        traineeId = db.getUserIdByEmail(email);

        tvStatWorkouts.setText(String.valueOf(db.getCompletedWorkouts(traineeId)));
        tvStatCalories.setText(String.valueOf(db.getTotalCalories(traineeId)));
        tvStatWeight.setText(db.getUserWeightByEmail(email) + " kg");

        // card clicks
        cardWorkouts.setOnClickListener(v -> {
            Intent i = new Intent(this, ClientWorkoutsActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardProgress.setOnClickListener(v -> {
            Intent i = new Intent(this, ClientProgressActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardMeals.setOnClickListener(v -> {
            Intent i = new Intent(this, TrackMealsActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardEditProfile.setOnClickListener(v -> {
            Intent i = new Intent(this, EditClientProfileActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardBMICalculator.setOnClickListener(v -> {
            Intent i = new Intent(this, BMIActivity.class);
            startActivity(i);
        });

        cardWorkoutCalendar.setOnClickListener(v -> {
            Intent i = new Intent(this, CalendarActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardChooseTrainer.setOnClickListener(v -> {
            Intent i = new Intent(this, TrainerListActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        cardNotification.setOnClickListener(v -> {
            Intent i = new Intent(this, NotificationActivity.class);
            i.putExtra("userId", traineeId);
            startActivity(i);
        });
    }
}