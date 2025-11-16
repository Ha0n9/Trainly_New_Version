package com.example.trainly;

import android.os.Bundle;
import android.database.Cursor;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClientWorkoutsActivity extends AppCompatActivity {

    RecyclerView recyclerClientWorkouts;
    ArrayList<Workout> workoutList;
    WorkoutAdapter workoutAdapter;

    DatabaseHelper db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_workouts);

        db = new DatabaseHelper(this);

        recyclerClientWorkouts = findViewById(R.id.recyclerClientWorkouts);
        workoutList = new ArrayList<>();

        // ✔ Lấy email từ Intent
        email = getIntent().getStringExtra("email");

        if (email == null) {
            finish();
            return;
        }

        // ✔ Lấy trainee ID
        int traineeId = db.getUserIdByEmail(email);

        // ✔ Load workouts từ DB
        loadAssignedWorkouts(traineeId);

        // ✔ Set adapter
        workoutAdapter = new WorkoutAdapter(workoutList);
        recyclerClientWorkouts.setLayoutManager(new LinearLayoutManager(this));
        recyclerClientWorkouts.setAdapter(workoutAdapter);
    }

    private void loadAssignedWorkouts(int traineeId) {
        Cursor c = db.getAssignedWorkouts(traineeId);

        if (c == null) return;

        while (c.moveToNext()) {
            String title = c.getString(1);
            String description = c.getString(2);
            int completed = c.getInt(3);

            workoutList.add(new Workout(
                    title,
                    description,
                    completed == 1  // true = completed
            ));
        }

        c.close();
    }
}
