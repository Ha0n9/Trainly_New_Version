package com.example.trainly;

public class TraineeProgressItem {
    public int traineeId;
    public String name;
    public int totalAssigned;
    public int completed;

    public TraineeProgressItem(int traineeId, String name, int totalAssigned, int completed) {
        this.traineeId = traineeId;
        this.name = name;
        this.totalAssigned = totalAssigned;
        this.completed = completed;
    }

    public int getCompletionPercentage() {
        if (totalAssigned == 0) return 0;
        return (completed * 100) / totalAssigned;
    }

    public String getProgressText() {
        return completed + " / " + totalAssigned;
    }
}