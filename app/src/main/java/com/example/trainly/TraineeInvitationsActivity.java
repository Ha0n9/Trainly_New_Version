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

public class TraineeInvitationsActivity extends AppCompatActivity {

    RecyclerView recycler;
    TextView tvEmpty;
    ImageView btnBack;
    DatabaseHelper db;
    int traineeId;
    ArrayList<TrainerInvitationItem> invitations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_invitations);

        recycler = findViewById(R.id.recyclerTrainerInvitations);
        tvEmpty = findViewById(R.id.tvEmptyInvitations);
        btnBack = findViewById(R.id.btnBack);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);
        traineeId = getIntent().getIntExtra("traineeId", -1);

        if (traineeId == -1) {
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());

        loadInvitations();
    }

    private void loadInvitations() {
        invitations.clear();

        Cursor c = db.getPendingTrainerInvitations(traineeId);
        if (c != null) {
            while (c.moveToNext()) {
                int invitationId = c.getInt(0);
                int trainerId = c.getInt(1);
                String trainerName = c.getString(2);
                String trainerEmail = c.getString(3);

                invitations.add(new TrainerInvitationItem(
                        invitationId, trainerId, trainerName, trainerEmail
                ));
            }
            c.close();
        }

        if (invitations.isEmpty()) {
            recycler.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

            TraineeInvitationAdapter adapter = new TraineeInvitationAdapter(
                    this, invitations, traineeId, db
            );
            recycler.setAdapter(adapter);
        }
    }

    public void refreshInvitations() {
        loadInvitations();
    }
}
