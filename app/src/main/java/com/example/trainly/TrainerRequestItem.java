package com.example.trainly;

public class TrainerRequestItem {
    public int requestId;
    public int traineeId;
    public String name;
    public String email;
    public int age;
    public double height;
    public double weight;

    public TrainerRequestItem(int requestId,
                              int traineeId,
                              String name,
                              String email,
                              int age,
                              double height,
                              double weight) {
        this.requestId = requestId;
        this.traineeId = traineeId;
        this.name = name;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }
}
