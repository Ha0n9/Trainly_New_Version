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

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recycler;
    TextView tvEmpty, tvTitle, tvMarkAllRead;
    ImageView btnBack;
    DatabaseHelper db;
    ArrayList<NotificationItem> list = new ArrayList<>();
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recycler = findViewById(R.id.recyclerNotifications);
        tvEmpty = findViewById(R.id.tvEmptyNotifications);
        tvTitle = findViewById(R.id.tvNotificationTitle);
        tvMarkAllRead = findViewById(R.id.tvMarkAllRead);
        btnBack = findViewById(R.id.btnBack);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);

        userId = getIntent().getIntExtra("user_id", -1);

        if (userId == -1) {
            finish();
            return;
        }

        setupClickListeners();
        loadNotifications();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        tvMarkAllRead.setOnClickListener(v -> {
            db.markAllNotificationsAsRead(userId);
            loadNotifications(); // Refresh list
        });
    }

    private void loadNotifications() {
        list.clear();

        Cursor c = db.getNotifications(userId);

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String message = c.getString(1);
            long time = c.getLong(2);
            int isRead = c.getInt(3);

            list.add(new NotificationItem(id, message, time, isRead));
        }
        c.close();

        if (list.isEmpty()) {
            recycler.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
            tvMarkAllRead.setVisibility(View.GONE);
        } else {
            recycler.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            tvMarkAllRead.setVisibility(View.VISIBLE);

            NotificationAdapter adapter = new NotificationAdapter(this, list, db);
            recycler.setAdapter(adapter);
        }
    }
}