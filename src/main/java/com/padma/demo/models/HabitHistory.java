package com.padma.demo.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Data // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // constructor vacío
@AllArgsConstructor // constructor con todos los campos
@Entity
@Table(name = "habit_histories")

public class HabitHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_history_id", nullable = false)
    private long habitHistoryId;

    @Column(name = "current_streak")
    private int currentStreak;

    @Column(name = "longest_streak")
    private int longestStreak;

    @Column(name = "completion_dates")
    private List<LocalDate> completionDates; // Fechas completadas como colección de valores simples

    @OneToOne
    private Habit habit; // Relación uno a uno con Habit

    @OneToMany(mappedBy = "habitHistories", cascade = CascadeType.ALL)
    private List<HabitCompletion> completions = new ArrayList<>();

}