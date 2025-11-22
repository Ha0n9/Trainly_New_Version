package com.example.trainly;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
    TextView tvTotalCaloriesToday;

    ArrayList<MealItem> mealList = new ArrayList<>();
    BarChart chart;

    DatabaseHelper db;
    int userId;

    ArrayList<Integer> weeklyCalories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_meals);

        // ====== INIT ======
        String email = getIntent().getStringExtra("email");
        db = new DatabaseHelper(this);
        userId = db.getUserIdByEmail(email);

        etMealName = findViewById(R.id.etMealName);
        etCalories = findViewById(R.id.etCalories);
        btnAddMeal = findViewById(R.id.btnAddMeal);
        tvTotalCaloriesToday = findViewById(R.id.tvTotalCaloriesToday);

        rvMeals = findViewById(R.id.rvMeals);
        rvMeals.setLayoutManager(new LinearLayoutManager(this));

        // FIX: Load meals from DB with null check
        loadMealsToday();

        adapter = new MealAdapter(mealList);
        rvMeals.setAdapter(adapter);

        // Update total calories display
        updateTotalCalories();

        // Load bar chart calories
        weeklyCalories = db.getWeeklyCalories(userId);
        chart = findViewById(R.id.barChart);
        setupBarChart();

        // ===== ADD MEAL =====
        btnAddMeal.setOnClickListener(v -> addMeal());

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText("Track Meals");
        }
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        enableSwipeToDelete();
    }

    private void addMeal() {
        String name = etMealName.getText().toString().trim();
        String calStr = etCalories.getText().toString().trim();

        if (name.isEmpty() || calStr.isEmpty()) {
            Toast.makeText(this, "Please enter meal name and calories", Toast.LENGTH_SHORT).show();
            return;
        }

        int calories = Integer.parseInt(calStr);

        // INSERT INTO DB
        boolean success = db.insertMeal(userId, name, calories);

        if (success) {
            // Reload meals to get the correct ID
            mealList.clear();
            loadMealsToday();
            adapter.notifyDataSetChanged();

            // Update total
            updateTotalCalories();

            // CLEAR inputs
            etMealName.setText("");
            etCalories.setText("");

            // Refresh bar chart
            weeklyCalories = db.getWeeklyCalories(userId);
            setupBarChart();

            Toast.makeText(this, "Meal added!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add meal", Toast.LENGTH_SHORT).show();
        }
    }

    // FIX: Null check added
    private void loadMealsToday() {
        Cursor c = db.getMealsTodayWithId(userId);

        if (c == null) return;

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            int calories = c.getInt(2);
            mealList.add(new MealItem(id, name, calories));
        }
        c.close();
    }

    // NEW: Update total calories display
    private void updateTotalCalories() {
        int total = 0;
        for (MealItem item : mealList) {
            total += item.getCalories();
        }
        tvTotalCaloriesToday.setText("Total Today: " + total + " kcal");
    }

    // FIX: Delete from database
    private void enableSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView r, RecyclerView.ViewHolder vH, RecyclerView.ViewHolder t) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder holder, int direction) {
                        int pos = holder.getAdapterPosition();

                        // FIX: Delete from database first
                        MealItem item = mealList.get(pos);
                        boolean deleted = db.deleteMeal(item.getId());

                        if (deleted) {
                            adapter.removeItem(pos);
                            updateTotalCalories();

                            // Refresh chart
                            weeklyCalories = db.getWeeklyCalories(userId);
                            setupBarChart();

                            Toast.makeText(TrackMealsActivity.this, "Meal deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TrackMealsActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(pos);
                        }
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

        // FIX: Set axis colors
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setAxisMaximum(maxVal + 300);
        chart.getAxisLeft().setTextColor(Color.WHITE);  // FIX: Add text color
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextColor(Color.WHITE);    // FIX: Legend color

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
