package com.padma.demo.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario users;

    @Column(name="messages")
    private String message;

    @Column(name="read_status")
    private boolean read;             // por si fue leida

    @Column(name="timestamps", nullable = false)
    private LocalDateTime timestamp;  // Feccha y hora de creación de la noti

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habits;  // referencia al hábito asociado a la notificación

    public Notification() {
        this.read = false;
        this.timestamp = LocalDateTime.now();
    }

    public Notification(Usuario user, String message) {
        this.users = users;
        this.message = message;
        this.read = false;
        this.timestamp = LocalDateTime.now();
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUser() { return users; }
    public void setUser(Usuario user) { this.users = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getTime() { return timestamp; }
    public void setTime(LocalDateTime time) { this.timestamp = time; }
}