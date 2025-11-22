package com.example.trainly;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerListActivity extends AppCompatActivity {

    RecyclerView recycler;
    DatabaseHelper db;
    TrainerAdapter adapter;

    ArrayList<TrainerItem> trainerList = new ArrayList<>();
    int traineeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_list);

        db = new DatabaseHelper(this);

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText("Choose Trainer");
        }
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        String email = getIntent().getStringExtra("email");
        traineeId = db.getUserIdByEmail(email);

        recycler = findViewById(R.id.recyclerTrainerList);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        loadTrainerList();
    }

    private void loadTrainerList() {
        trainerList.clear();

        Cursor c = db.getAllTrainers();
        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String email = c.getString(2);

            trainerList.add(new TrainerItem(id, name, email));
        }
        c.close();

        adapter = new TrainerAdapter(this, trainerList, traineeId, db);
        recycler.setAdapter(adapter);
    }
}
