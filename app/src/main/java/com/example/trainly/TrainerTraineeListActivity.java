package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerTraineeListActivity extends AppCompatActivity {

    RecyclerView recycler;
    TextView tvEmpty;
    ImageView btnBack;
    DatabaseHelper db;
    int trainerId;
    ArrayList<TrainerRequestItem> trainees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_trainee_list);

        recycler = findViewById(R.id.recyclerTrainerTrainees);
        tvEmpty = findViewById(R.id.tvEmptyTrainees);
        btnBack = findViewById(R.id.btnBack);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);

        trainerId = getIntent().getIntExtra("trainerId", -1);

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) toolbarTitle.setText("My Trainees");
        btnBack.setOnClickListener(v -> finish());

        loadTrainees();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning from detail view
        loadTrainees();
    }

    private void loadTrainees() {
        Cursor c = db.getTrainerTrainees(trainerId);

        trainees.clear();

        while (c.moveToNext()) {
            int traineeId = c.getInt(0);
            String name = c.getString(1);
            String email = c.getString(2);
            int age = c.getInt(3);
            int height = c.getInt(4);
            int weight = c.getInt(5);

            trainees.add(new TrainerRequestItem(-1, traineeId, name, email, age, height, weight));
        }

        c.close();

        if (trainees.isEmpty()) {
            recycler.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

            TrainerTraineeListAdapter adapter =
                    new TrainerTraineeListAdapter(this, trainees, trainerId);
            recycler.setAdapter(adapter);
        }
    }
}
