package com.example.trainly;

public class WorkoutHistoryItem {

    private String date;
    private String title;
    private String summary;
    private boolean isCompleted;

    public WorkoutHistoryItem(String date, String title, String summary, boolean isCompleted) {
        this.date = date;
        this.title = title;
        this.summary = summary;
        this.isCompleted = isCompleted;
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

    public boolean isCompleted() {
        return isCompleted;
    }
}
