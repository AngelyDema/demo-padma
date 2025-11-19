package com.padma.demo.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.padma.demo.models.HabitHistory;

@Data // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor // constructor vacío
@AllArgsConstructor // constructor con todos los campos
@Entity
@Table(name = "habits")

public class Habit {

    // Atributos

    @Id
    @Column(name = "habit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long habitId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "goal")
    private int goal; // elegida por el User, está definida por cierta cantidad de rachas (días
                      // completados cierto hábito)

    @OneToOne(mappedBy = "habit", cascade = CascadeType.ALL)
    private HabitHistory habitHistory; // Relación uno a uno con HabitHistory

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users; // Muchos hábitos pueden pertenecer a un User

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area areas; // Muchos hábitos pueden pertenecer a un área

}
