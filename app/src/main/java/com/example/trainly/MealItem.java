package com.example.trainly;

public class MealItem {
    int id;
    String name;
    int calories;

    public MealItem(int id, String name, int calories) {
        this.id = id;
        this.name = name;
        this.calories = calories;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public int getCalories() { return calories; }
}