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

public class StartWorkoutActivity extends AppCompatActivity {

    RecyclerView recyclerStartWorkout;
    ArrayList<Exercise> exerciseList;
    StartWorkoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_workout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerStartWorkout = findViewById(R.id.recyclerStartWorkout);

        // Fake exercises
        exerciseList = new ArrayList<>();
        exerciseList.add(new Exercise("Bench Press", "4 x 8"));
        exerciseList.add(new Exercise("Shoulder Press", "4 x 10"));
        exerciseList.add(new Exercise("Cable Fly", "3 x 12"));
        exerciseList.add(new Exercise("Tricep Dip", "3 x 15"));

        adapter = new StartWorkoutAdapter(exerciseList);
        recyclerStartWorkout.setLayoutManager(new LinearLayoutManager(this));
        recyclerStartWorkout.setAdapter(adapter);
    }
}