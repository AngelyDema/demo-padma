package com.padma.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "habit_histories")
public class HabitHistory {

    @Id
    private String id;

    private int currentStreak;
    private int longestStreak;

    
    private List<LocalDate> completionDates; // Fechas completadas como colección de valores simples


    private LocalDate createdAt;

    public HabitHistory() {}

    public HabitHistory(int currentStreak, int longestStreak, List<LocalDate> completionDates, LocalDate createdAt) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.completionDates = completionDates;
        this.goal = goal;
        this.createdAt = createdAt;
    }

    // Getters y setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public List<LocalDate> getCompletionDates() { return completionDates; }
    public void setCompletionDates(List<LocalDate> completionDates) { this.completionDates = completionDates; }

    public int getGoal() { return goal; }
    public void setGoal(int goal) { this.goal = goal; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
