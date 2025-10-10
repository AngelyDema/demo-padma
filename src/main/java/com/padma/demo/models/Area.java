package com.padma.demo.models;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "areas")
public class Area {
  @Id
    private String id;
    
    private String name;
    private String description;

    //un área puede tener muchos hábitos
      @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habit> habits = new ArrayList<>();

    public Area(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addHabit(Habit habit) {
        habits.add(habit);
    }

    public void removeHabit(Habit habit) {
        habits.remove(habit);
    }

    // Getters y setters
    public List<Habit> getHabits() {
        return habits;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}