package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerPendingRequestsActivity extends AppCompatActivity {

    RecyclerView recycler;
    DatabaseHelper db;
    ArrayList<TrainerRequestItem> list = new ArrayList<>();
    int trainerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_pending_requests);

        recycler = findViewById(R.id.recyclerTrainerRequests);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);

        // Lấy trainerId từ Intent
        trainerId = getIntent().getIntExtra("trainerId", -1);

        if (trainerId == -1) {
            Toast.makeText(this, "Trainer ID not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRequests();
    }

    private void loadRequests() {
        list.clear();

        Cursor c = db.getPendingTrainerRequests(trainerId);
        if (c != null) {
            while (c.moveToNext()) {

                int requestId = c.getInt(0);
                int traineeId = c.getInt(1);
                String name = c.getString(2);
                String email = c.getString(3);
                int age = c.getInt(4);
                int height = c.getInt(5);
                int weight = c.getInt(6);

                list.add(new TrainerRequestItem(
                        requestId, traineeId, name, email, age, height, weight
                ));
            }
            c.close();
        }

        TrainerRequestAdapter adapter = new TrainerRequestAdapter(this, list, db, trainerId);
        recycler.setAdapter(adapter);
    }
}
