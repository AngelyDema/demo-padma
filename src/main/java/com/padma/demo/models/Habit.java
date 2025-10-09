package com.padma.demo.models;
import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.padma.demo.services.HabitServices;

@Entity
@Table(name = "habits")
    public class Habit {

     //Atributos
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne
    private Usuario user;

    private String name;
    private String description;
    private String frequency;
    private LocalDate createdAt;
    private boolean completed;
    private int streak;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HabitServices> completions = new ArrayList<>();

    public Habit(String id, Usuario user, String name, String description, String color, String frequency) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = LocalDate.now();
        this.completed = false;
        this.streak = 0;
    }

    // Getters y Setters
    public String getId() { return id; }
    public Usuario getUser() { return user; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getFrequency() { return frequency; }
    public LocalDate getCreatedAt() { return createdAt; }
    public boolean isCompleted() { return completed; }
    public int getStreak() { return streak; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setStreak(int streak) { this.streak = streak; }

    public List<HabitServices> getCompletions() {
        return completions;
    }

    public void addCompletion(HabitServices completion) {
        completions.add(completion);
        completion.setHabit(this); // importancia para mantener consistencia bidireccional
    }

    public void removeCompletion(HabitServices completion) {
        completions.remove(completion);
        completion.setHabit(null);
    }
}
    



