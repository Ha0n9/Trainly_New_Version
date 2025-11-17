package com.example.trainly;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recycler;
    DatabaseHelper db;
    ArrayList<NotificationItem> list = new ArrayList<>();
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recycler = findViewById(R.id.recyclerNotifications);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        db = new DatabaseHelper(this);

        userId = getIntent().getIntExtra("userId", -1);

        loadNotifications();
    }

    private void loadNotifications() {
        list.clear();

        Cursor c = db.getNotifications(userId);

        while (c.moveToNext()) {
            String message = c.getString(1);
            long time = c.getLong(2);
            int isRead = c.getInt(3);

            list.add(new NotificationItem(message, time, isRead));
        }
        c.close();

        NotificationAdapter adapter = new NotificationAdapter(this, list);
        recycler.setAdapter(adapter);
    }
}
