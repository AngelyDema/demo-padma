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
    @ManyToOne
    private Usuario user;
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

    public void markAsCompleted() {
    this.completed = true;
    this.completedAt = LocalDate.now();
}

private static int prioridadValue(String prioridad) {
        if (prioridad == null) return Integer.MAX_VALUE;
        switch(prioridad.toLowerCase()) {
            case "high": return 1;
            case "medium": return 2;
            case "low": return 3;
            default: return Integer.MAX_VALUE;
        }
    }

    // Ordena la lista por prioridad
    public static void sortByPriority(List<Todo> todos) {
        todos.sort(Comparator.comparingInt(todo -> prioridadValue(todo.getPriority())));
    }

    // Getters y Setters
    public String getPriority() {
        return this.priority;
    }
}

