package com.padma.demo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @Column(name="messages")
    private String message;

    @Column(name="read_status")
    private boolean read;             // por si fue leida

    @Column(name="timestamps", nullable = false)
    private LocalDateTime timestamp;  // Feccha y hora de creación de la noti

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habits;  // referencia al hábito asociado a la notificación

   
}