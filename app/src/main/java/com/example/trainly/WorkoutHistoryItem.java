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

    public WorkoutHistoryItem(String title, int calories, String status, long rawTimestamp) {

        this.title = title;

        // summary = calor + status
        this.summary = "Calories: " + calories + " • " + status;

        // convert timestamp → "dd/MM/yyyy HH:mm"
        this.date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                .format(new java.util.Date(rawTimestamp));

        this.isCompleted = status.equalsIgnoreCase("completed");
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
