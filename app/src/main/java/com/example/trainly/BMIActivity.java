package com.example.trainly;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BMIActivity extends AppCompatActivity {

    EditText etBMIWeight, etBMIHeight;
    Button btnBMICalc;
    CardView cardBMIResult;
    TextView tvBMIValue, tvBMICategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmiactivity);

        etBMIWeight = findViewById(R.id.etBMIWeight);
        etBMIHeight = findViewById(R.id.etBMIHeight);
        btnBMICalc = findViewById(R.id.btnBMICalc);
        cardBMIResult = findViewById(R.id.cardBMIResult);
        tvBMIValue = findViewById(R.id.tvBMIValue);
        tvBMICategory = findViewById(R.id.tvBMICategory);

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText(getString(R.string.bmi_title));
        }
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnBMICalc.setOnClickListener(v -> calculateBMI());
    }

    private void calculateBMI() {
        String wStr = etBMIWeight.getText().toString().trim();
        String hStr = etBMIHeight.getText().toString().trim();

        if (wStr.isEmpty() || hStr.isEmpty()) {
            Toast.makeText(this, "Please enter weight and height", Toast.LENGTH_SHORT).show();
            return;
        }

        double weight = Double.parseDouble(wStr);
        double height = Double.parseDouble(hStr) / 100.0; // convert cm → meters

        if (weight <= 0 || height <= 0) {
            Toast.makeText(this, "Invalid values", Toast.LENGTH_SHORT).show();
            return;
        }

        double bmi = weight / (height * height);

        String category;
        int color;

        if (bmi < 18.5) {
            category = "Underweight";
            color = Color.parseColor("#4DA3FF"); // light blue
        } else if (bmi < 24.9) {
            category = "Normal";
            color = Color.parseColor("#37D67A"); // green
        } else if (bmi < 29.9) {
            category = "Overweight";
            color = Color.parseColor("#FFC107"); // yellow
        } else {
            category = "Obese";
            color = Color.parseColor("#FF5252"); // red
        }

        tvBMIValue.setText(String.format("%.1f", bmi));
        tvBMICategory.setText(category);
        tvBMIValue.setTextColor(color);

        cardBMIResult.setVisibility(View.VISIBLE);
    }
}
