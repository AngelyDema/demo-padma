package com.padma.demo.models;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "listtodos")
public class ListTodo {

    //Atributos
@Id
    private String id;

    private String name;
    private String description;

    // Agregación: una lista puede contener muchas tareas

    @OneToMany(mappedBy = "listTodo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    public ListTodo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addTodo(Todo todo) {
        todos.add(todo);
    }

    public void removeTodo(Todo todo) {
        todos.remove(todo);
    }

    // Getters y setters
    public List<Todo> getTodos() {
        return todos;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
