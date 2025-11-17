package com.example.trainly;

public class NotificationItem {
    String message;
    long timestamp;
    int isRead;

    public NotificationItem(String message, long timestamp, int isRead) {
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }
}
