package com.padma.demo.models;
import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
    public class Habit {

     //Atributos
    
    @Id
    private String habit_id;
    
    @Column(name="title", nullable = false)
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="frequencies")
    private String frequency;

    @Column(name="created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name="completed", nullable = false)
    private boolean completed;
    @Column(name="goal")
    private int goal; //elegida por el usuario, está definida por cierta cantidad de rachas (días completados cierto hábito)

    @OneToOne(mappedBy = "habit", cascade = CascadeType.ALL)
    private HabitHistory habitHistory;   //Relación uno a uno con HabitHistory

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario users;  //Muchos hábitos pueden pertenecer a un usuario

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area areas;  //Muchos hábitos pueden pertenecer a un área

    public Habit() {}

    public Habit(String  habit_id, Usuario user, String title, String description, String color, String frequency, int goal) {
        this. habit_id =  habit_id;
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.createdAt = LocalDate.now();
        this.completed = false;
        this.goal = goal;}

    // Getters y Setters
    public String getHabit_id() { return  habit_id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getFrequency() { return frequency; }
    public LocalDate getCreatedAt() { return createdAt; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public int getGoal() { return goal; }
    public void setGoal(int goal) { this.goal = goal; }

}
    



