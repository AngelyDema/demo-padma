package com.padma.demo.models;

import java.util.ArrayList;
import java.util.List;

public class ListTodo {

    private String id;
    private String name;

    // Agregación: una lista puede contener muchas tareas
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
