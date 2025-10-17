package com.padma.demo.models;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "areas")
public class Area {
    @Id
    private String area_id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="description")
    private String description;

    @OneToMany(mappedBy = "areas", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Habit> habits = new ArrayList<>(); // Agregación: un área puede contener muchos hábitos

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario users;  //Muchas áreas pueden pertenecer a un usuario

    public Area() {} 

    public Area(String area_id, String name) {
        this.area_id= area_id;
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

    public String getArea_id() {
        return area_id;
    }
}