package com.example.trainly;

public class MealItem {
    String name;
    int calories;

    public MealItem(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    public String getName() { return name; }

    public int getCalories() { return calories; }
}
