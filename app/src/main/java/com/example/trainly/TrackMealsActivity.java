package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TrackMealsActivity extends AppCompatActivity {

    EditText etMealName, etCalories;
    LinearLayout mealList;
    Button btnAddMeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_meals);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etMealName = findViewById(R.id.etMealName);
        etCalories = findViewById(R.id.etCalories);
        mealList = findViewById(R.id.mealList);
        btnAddMeal = findViewById(R.id.btnAddMeal);

        btnAddMeal.setOnClickListener(v -> {
            String name = etMealName.getText().toString();
            String cal = etCalories.getText().toString();

            if (name.isEmpty() || cal.isEmpty()) return;

            // thêm 1 dòng vào meal list
            TextView tv = new TextView(this);
            tv.setText("- " + name + " (" + cal + " kcal)");
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setTextSize(16);

            mealList.addView(tv);

            etMealName.setText("");
            etCalories.setText("");
        });
    }
}