package com.example.trainly;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class TrainerReminderActivity extends AppCompatActivity {

    Spinner spTrainee;
    EditText etMessage;
    Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trainer_reminder);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spTrainee = findViewById(R.id.spTraineeReminder);
        etMessage = findViewById(R.id.etMessageReminder);
        btnSend = findViewById(R.id.btnSendReminder);

        List<String> traineeList = new ArrayList<>();
        traineeList.add("Select trainee");
        traineeList.add("Alex");
        traineeList.add("Maria");
        traineeList.add("John");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_text,
                traineeList
        );

        adapter.setDropDownViewResource(R.layout.spinner_dropdown);

        spTrainee.setAdapter(adapter);
        spTrainee.setSelection(0);

        btnSend.setOnClickListener(v -> finish());
    }
}
