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

public class TrainerInviteTraineeActivity extends AppCompatActivity {
    RecyclerView recycler;
    TextView tvEmpty;
    ImageView btnBack;
    DatabaseHelper db;
    int trainerId;
    ArrayList<TraineeItem> trainees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_invite_trainee);
        recycler = findViewById(R.id.recyclerAvailableTrainees);
        tvEmpty = findViewById(R.id.tvEmptyAvailableTrainees);
        btnBack = findViewById(R.id.btnBack);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);
        trainerId = getIntent().getIntExtra("trainerId", -1);
        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) toolbarTitle.setText("Invite Trainee");
        btnBack.setOnClickListener(v -> finish());
        loadAvailableTrainees();
    }

    private void loadAvailableTrainees() {
        trainees.clear();
        // Get all clients (trainees) without a trainer
        Cursor c = db.getReadableDatabase().rawQuery(
                "SELECT id, name, email, age, height, weight FROM users " +
                        "WHERE role='client' AND id NOT IN " +
                        "(SELECT trainee_id FROM trainer_trainee)",
                null
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String email = c.getString(2);
            int age = c.getInt(3);
            int height = c.getInt(4);
            int weight = c.getInt(5);
            trainees.add(new TraineeItem(id, name, email, age, height, weight));
        }

        c.close();

        if (trainees.isEmpty()) {
            recycler.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            TrainerInviteAdapter adapter = new TrainerInviteAdapter(this, trainees, trainerId, db);
            recycler.setAdapter(adapter);
        }
    }

    public void refreshList() {
        loadAvailableTrainees();
    }
}
