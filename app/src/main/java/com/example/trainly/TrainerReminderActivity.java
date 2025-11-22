package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TrainerReminderActivity extends AppCompatActivity {

    Spinner spTraineeReminder;
    EditText etReminderMessage;
    Button btnSendReminder;

    DatabaseHelper db;
    int trainerId = -1;

    ArrayList<Integer> traineeIds = new ArrayList<>();
    ArrayList<String> traineeNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_reminder);

        db = new DatabaseHelper(this);
        trainerId = getIntent().getIntExtra("trainer_id", -1);

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText(getString(R.string.reminder_title));
        }
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        spTraineeReminder = findViewById(R.id.spTraineeReminder);
        etReminderMessage = findViewById(R.id.etMessageReminder);
        btnSendReminder = findViewById(R.id.btnSendReminder);

        loadTrainees();

        btnSendReminder.setOnClickListener(v -> {
            if (traineeIds.isEmpty()) {
                Toast.makeText(this, "No trainees available", Toast.LENGTH_SHORT).show();
                return;
            }

            String msg = etReminderMessage.getText().toString().trim();
            if (msg.isEmpty()) {
                Toast.makeText(this, "Enter reminder message", Toast.LENGTH_SHORT).show();
                return;
            }

            int pos = spTraineeReminder.getSelectedItemPosition();
            int traineeId = traineeIds.get(pos);
            String name = traineeNames.get(pos);

            db.insertNotification(traineeId, msg);

            Toast.makeText(this,
                    "Reminder sent to " + name + ":\n" + msg,
                    Toast.LENGTH_LONG).show();

            etReminderMessage.setText("");
        });
    }

    private void loadTrainees() {
        traineeIds.clear();
        traineeNames.clear();

        if (trainerId == -1) return;

        Cursor c = db.getTraineesForTrainer(trainerId);
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(0);
                String name = c.getString(1);
                String email = c.getString(2);

                traineeIds.add(id);
                if (name == null || name.trim().isEmpty()) {
                    traineeNames.add(email);
                } else {
                    traineeNames.add(name);
                }
            }
            c.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_text,
                traineeNames
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spTraineeReminder.setAdapter(adapter);
    }
}
