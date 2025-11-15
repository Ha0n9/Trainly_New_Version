package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddExerciseActivity extends AppCompatActivity {

    EditText etName, etSets, etReps, etNotes;
    Button btnAddExercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_exercise);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etExName);
        etSets = findViewById(R.id.etExSets);
        etReps = findViewById(R.id.etExReps);
        etNotes = findViewById(R.id.etExNotes);
        btnAddExercise = findViewById(R.id.btnAddExercise);

        btnAddExercise.setOnClickListener(v -> finish());
    }
}