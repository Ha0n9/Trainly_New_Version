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

public class ExerciseListActivity extends AppCompatActivity {

    RecyclerView recyclerExercises;
    ArrayList<Exercise> exerciseList;
    ExerciseAdapter exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerExercises = findViewById(R.id.recyclerExercises);

        exerciseList = new ArrayList<>();
        exerciseList.add(new Exercise("Bench Press", "4 sets x 8 reps"));
        exerciseList.add(new Exercise("Shoulder Press", "4 sets x 10 reps"));
        exerciseList.add(new Exercise("Tricep Dips", "3 sets x 12 reps"));
        exerciseList.add(new Exercise("Incline Dumbbell Press", "3 sets x 10 reps"));

        exerciseAdapter = new ExerciseAdapter(exerciseList);
        recyclerExercises.setLayoutManager(new LinearLayoutManager(this));
        recyclerExercises.setAdapter(exerciseAdapter);
    }
}