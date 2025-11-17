package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CurrentTrainerActivity extends AppCompatActivity {

    DatabaseHelper db;
    int traineeId;

    TextView tvName, tvEmail;
    Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trainer);

        db = new DatabaseHelper(this);

        traineeId = getIntent().getIntExtra("traineeId", -1);
        if (traineeId == -1) {
            Toast.makeText(this, "Invalid trainee ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName = findViewById(R.id.tvTrainerName);
        tvEmail = findViewById(R.id.tvTrainerEmail);
        btnChange = findViewById(R.id.btnChangeTrainer);

        loadTrainer();

        btnChange.setOnClickListener(v -> {
            boolean ok = db.unassignTrainer(traineeId);

            if (ok) {
                Toast.makeText(this, "Trainer removed. You can choose a new trainer.", Toast.LENGTH_SHORT).show();

                // Finish activity → return back to choose trainer
                finish();
            } else {
                Toast.makeText(this, "You don't have any trainer to remove.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadTrainer() {
        Cursor c = db.getCurrentTrainer(traineeId);

        if (c != null && c.moveToFirst()) {

            String name = c.getString(c.getColumnIndexOrThrow("trainer_name"));
            String email = c.getString(c.getColumnIndexOrThrow("trainer_email"));

            tvName.setText(name);
            tvEmail.setText(email);

            c.close();
        } else {
            tvName.setText("No trainer assigned");
            tvEmail.setText("");
            btnChange.setText("Choose Trainer");
        }

    }
}
