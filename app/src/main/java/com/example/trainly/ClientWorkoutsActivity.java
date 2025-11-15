package com.example.trainly;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClientWorkoutsActivity extends AppCompatActivity {

    RecyclerView recyclerClientWorkouts;
    ArrayList<Workout> workoutList;
    WorkoutAdapter workoutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_workouts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerClientWorkouts = findViewById(R.id.recyclerClientWorkouts);

        workoutList = new ArrayList<>();
        workoutList.add(new Workout("Push Day", "Chest + Triceps + Shoulders", false));
        workoutList.add(new Workout("Leg Day", "Quads + Glutes + Hamstrings", true));
        workoutList.add(new Workout("Pull Day", "Back + Biceps", false));

        workoutAdapter = new WorkoutAdapter(workoutList);
        recyclerClientWorkouts.setLayoutManager(new LinearLayoutManager(this));
        recyclerClientWorkouts.setAdapter(workoutAdapter);
    }
}