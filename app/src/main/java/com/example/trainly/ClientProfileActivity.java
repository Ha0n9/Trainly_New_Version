package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ClientProfileActivity extends AppCompatActivity {

    TextView tvGreeting;
    CardView cardWorkouts, cardProgress, cardMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_profile);

        tvGreeting = findViewById(R.id.tvGreeting);

        cardWorkouts = findViewById(R.id.cardWorkouts);
        cardProgress = findViewById(R.id.cardProgress);
        cardMeals = findViewById(R.id.cardMeals);

        String name = getIntent().getStringExtra("name");
        if (name != null) {
            tvGreeting.setText("Hi, " + name);
        }

        cardWorkouts.setOnClickListener(v ->
                startActivity(new Intent(this, ClientWorkoutsActivity.class)));

        cardProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ClientProgressActivity.class)));

        cardMeals.setOnClickListener(v ->
                startActivity(new Intent(this, TrackMealsActivity.class)));
    }
}