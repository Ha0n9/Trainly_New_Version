package com.example.trainly;

public class NotificationItem {
    int id;
    String message;
    long timestamp;
    int isRead;

    public NotificationItem(int id, String message, long timestamp, int isRead) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }
}