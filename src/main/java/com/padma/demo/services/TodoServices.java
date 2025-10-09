package com.padma.demo.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;
import com.padma.demo.models.Usuario;


public class TodoServices {
    //Atributos
    private String id;
    private Usuario user;
    private String title;
    private String description;
    private boolean completed;
    private String priority;
    private LocalDate dueDate;
    private LocalDate createdAt;
    private LocalDate completedAt;


    public void markAsCompleted() {
    this.completed = true;
    this.completedAt = LocalDate.now();
}

private static int prioridadValue(String prioridad) {
        if (prioridad == null) return Integer.MAX_VALUE;
        switch (prioridad.toLowerCase()) {
            case "high": return 1;
            case "medium": return 2;
            case "low": return 3;
            default: return Integer.MAX_VALUE;
        }
    }

    // Ordena la lista por prioridad
    public static void sortByPriority(List<TodoServices> todos) {
        todos.sort(Comparator.comparingInt(todo -> prioridadValue(todo.getPriority())));
    }

    // Getters y Setters
    public String getPriority() {
        return this.priority;
    }
}

