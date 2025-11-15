package com.example.trainly;

public class WorkoutHistoryItem {
    private final String date;
    private final String title;
    private final String summary;

    public WorkoutHistoryItem(String date, String title, String summary) {
        this.date = date;
        this.title = title;
        this.summary = summary;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }
}
