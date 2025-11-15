package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BMIActivity extends AppCompatActivity {

    EditText etWeight, etHeight;
    TextView tvResult;
    Button btnCalc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bmiactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etWeight = findViewById(R.id.etBMIWeight);
        etHeight = findViewById(R.id.etBMIHeight);
        tvResult = findViewById(R.id.tvBMIResult);
        btnCalc = findViewById(R.id.btnBMICalc);

        btnCalc.setOnClickListener(v -> {
            double w = Double.parseDouble(etWeight.getText().toString());
            double h = Double.parseDouble(etHeight.getText().toString()) / 100; // cm to m

            double bmi = w / (h * h);
            String category;

            if (bmi < 18.5) category = "Underweight";
            else if (bmi < 24.9) category = "Normal";
            else if (bmi < 29.9) category = "Overweight";
            else category = "Obese";

            tvResult.setText("Your BMI: " + String.format("%.1f", bmi) + " (" + category + ")");
        });
    }
}