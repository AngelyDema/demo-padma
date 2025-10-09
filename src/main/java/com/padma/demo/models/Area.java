package com.padma.demo.models;

import java.util.ArrayList;
import java.util.List;

public class Area {

    private String id;
    private String name;

    //un área puede tener muchos hábitos
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