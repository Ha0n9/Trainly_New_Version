package com.example.trainly;

public class TraineeItem {
    public int id;
    public String name;
    public String email;
    public int age;
    public int height;
    public int weight;

    public TraineeItem(int id, String name, String email, int age, int height, int weight) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }
}