package com.padma.demo.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "habit_histories")
public class HabitHistory {

    @Id
    private String habit_history_id;

    @Column(name="current_streak")
    private int currentStreak;

    @Column(name="longest_streak")
    private int longestStreak;

    @Column(name="completion_dates")
    private List<LocalDate> completionDates; // Fechas completadas como colección de valores simples

    @Column(name="created_at", nullable = false)
    private LocalDate createdAt;

    @OneToOne
    private Habit habit; // Relación uno a uno con Habit

    public HabitHistory() {}

    public HabitHistory(int currentStreak, int longestStreak, List<LocalDate> completionDates, LocalDate createdAt) {
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.completionDates = completionDates;
        this.createdAt = createdAt;
    }

    // Getters y setters

    public String getId() { return habit_history_id; }
    public void setId(String habit_history_id) { this.habit_history_id = habit_history_id; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public List<LocalDate> getCompletionDates() { return completionDates; }
    public void setCompletionDates(List<LocalDate> completionDates) { this.completionDates = completionDates; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}