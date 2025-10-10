package com.padma.demo.models;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;

@Entity
@Table(name = "todos")
public class Todo {
	
    //Atributos
    
    @Id
    private String id;


    private String title;
    private String description;
    private boolean completed;
    private String priority;
    private LocalDate dueDate;
    private LocalDate createdAt;
    private LocalDate completedAt;

    //Constructor
    public Todo(String id, Usuario user, String title, String description, String priority, LocalDate dueDate) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = LocalDate.now();
        this.completed = false;
    }

    //Getters y Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Usuario getUser() {
        return user;
    }
    public void setUser(Usuario user) {
        this.user = user;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    public LocalDate getCompletedAt() {
        return completedAt;
    }
    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }
    
}

