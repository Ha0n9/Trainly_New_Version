package com.example.trainly;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class TrackMealsActivity extends AppCompatActivity {

    EditText etMealName, etCalories;
    Button btnAddMeal;
    RecyclerView rvMeals;
    MealAdapter adapter;

    ArrayList<MealItem> mealList = new ArrayList<>();
    BarChart chart;

    DatabaseHelper db;
    int userId;

    ArrayList<Integer> weeklyCalories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_meals);

        // ====== INIT ======
        String email = getIntent().getStringExtra("email");
        db = new DatabaseHelper(this);
        userId = db.getUserIdByEmail(email);

        etMealName = findViewById(R.id.etMealName);
        etCalories = findViewById(R.id.etCalories);
        btnAddMeal = findViewById(R.id.btnAddMeal);

        rvMeals = findViewById(R.id.rvMeals);
        rvMeals.setLayoutManager(new LinearLayoutManager(this));

        // Load meals from DB
        loadMealsToday();

        adapter = new MealAdapter(mealList);
        rvMeals.setAdapter(adapter);

        // Load bar chart calories
        weeklyCalories = db.getWeeklyCalories(userId);
        chart = findViewById(R.id.barChart);
        setupBarChart();

        // ===== ADD MEAL =====
        btnAddMeal.setOnClickListener(v -> addMeal());

        enableSwipeToDelete();
    }

    private void addMeal() {
        String name = etMealName.getText().toString().trim();
        String calStr = etCalories.getText().toString().trim();

        if (name.isEmpty() || calStr.isEmpty()) return;

        int calories = Integer.parseInt(calStr);

        // INSERT INTO DB
        db.insertMeal(userId, name, calories);

        // UPDATE LIST UI
        mealList.add(new MealItem(name, calories));
        adapter.notifyItemInserted(mealList.size() - 1);

        // CLEAR
        etMealName.setText("");
        etCalories.setText("");

        // Refresh bar chart
        weeklyCalories = db.getWeeklyCalories(userId);
        setupBarChart();
    }

    private void loadMealsToday() {
        Cursor c = db.getMealsToday(userId);

        while (c.moveToNext()) {
            String name = c.getString(0);
            int calories = c.getInt(1);
            mealList.add(new MealItem(name, calories));
        }
        c.close();
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView r, RecyclerView.ViewHolder vH, RecyclerView.ViewHolder t) { return false; }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder holder, int direction) {
                        int pos = holder.getAdapterPosition();

                        // TODO optional: remove from DB using meal id (nếu cần)
                        adapter.removeItem(pos);
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(rvMeals);
    }

    private void setupBarChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        int maxVal = 0;

        for (int i = 0; i < weeklyCalories.size(); i++) {
            int cal = weeklyCalories.get(i);
            entries.add(new BarEntry(i, cal));
            maxVal = Math.max(maxVal, cal);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Calories");
        dataSet.setColor(Color.parseColor("#FF8C32"));
        dataSet.setValueTextColor(Color.WHITE);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.45f);

        chart.setData(data);
        chart.setFitBars(true);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(maxVal + 300);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setLabelCount(7);
        x.setTextColor(Color.WHITE);

        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
                int idx = (int) value;
                return (idx >= 0 && idx < days.length) ? days[idx] : "";
            }
        });

        chart.invalidate();
    }
}