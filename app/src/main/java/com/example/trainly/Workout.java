package com.example.trainly;

public class Workout {
    String title, subtitle;
    boolean completed;
    int assignedWorkoutId;
    int planId;

    public Workout(String title, String subtitle, boolean completed, int assignedWorkoutId, int planId) {
        this.title = title;
        this.subtitle = subtitle;
        this.completed = completed;
        this.assignedWorkoutId = assignedWorkoutId;
        this.planId = planId;
    }

    // Old constructor for backward compatibility
    public Workout(String t, String s, boolean c) {
        this.title = t;
        this.subtitle = s;
        this.completed = c;
        this.assignedWorkoutId = -1;
        this.planId = -1;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getAssignedWorkoutId() {
        return assignedWorkoutId;
    }

    public int getPlanId() {
        return planId;
    }
}