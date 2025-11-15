package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ClientProfileActivity extends AppCompatActivity {

    CardView cardWorkouts, cardProgress, cardMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cardWorkouts = findViewById(R.id.cardWorkouts);
        cardProgress = findViewById(R.id.cardProgress);
        cardMeals = findViewById(R.id.cardMeals);

        cardWorkouts.setOnClickListener(v ->
                startActivity(new Intent(this, ClientWorkoutsActivity.class)));

        cardProgress.setOnClickListener(v ->
                startActivity(new Intent(this, ClientProgressActivity.class)));

        cardMeals.setOnClickListener(v ->
                startActivity(new Intent(this, TrackMealsActivity.class)));
    }
}