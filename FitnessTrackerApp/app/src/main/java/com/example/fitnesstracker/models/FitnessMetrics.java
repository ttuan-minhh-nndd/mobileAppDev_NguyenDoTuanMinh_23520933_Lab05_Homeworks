package com.example.fitnesstracker.models;

public class FitnessMetrics {
    public final long steps;
    public final double calories;
    public final int points;

    public FitnessMetrics(long steps, double calories, int points) {
        this.steps = steps;
        this.calories = calories;
        this.points = points;
    }
}
