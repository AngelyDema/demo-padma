package com.padma.demo.models;
import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.padma.demo.models.HabitHistory;

@Entity
@Table(name = "habits")
    public class Habit {

     //Atributos
    
    @Id
    private String id;

    private String title;
    private String description;
    private String frequency;
    private LocalDate createdAt;
    private boolean completed;
    private int goal; //elegida por el usuario, está definida por cierta cantidad de rachas (días completados cierto hábito)

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HabitHistory> completionsDates = new ArrayList<>();

    public Habit(String id, Usuario user, String title, String description, String color, String frequency, int goal) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = LocalDate.now();
        this.completed = false;
        this.goal = goal;}

    // Getters y Setters
    public String getId() { return id; }
    public Usuario getUser() { return user; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFrequency() { return frequency; }
    public LocalDate getCreatedAt() { return createdAt; }
    public boolean isCompleted() { return completed; }
    public int getStreak() { return streak; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getGoal() { return goal; }
    public void setGoal(int goal) { this.goal = goal; }

    public List<HabitServices> getCompletions() {
        return completions;
    }

    public void addCompletion(HabitServices completion) {
        completions.add(completion);
        completion.setHabit(this); 
    }

    public void removeCompletion(HabitServices completion) {
        completions.remove(completion);
        completion.setHabit(null);
    }
}
    



