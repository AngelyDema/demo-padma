package com.padma.demo.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "list_todos")
public class ListTodo {

    //Atributos
@Id
    private String list_todo_id;

    @Column(name="names", nullable = false)
    private String name;

    @Column(name="descriptions")
    private String description;

    // Agregación: una lista puede contener muchas tareas
    @OneToMany(mappedBy = "list_todos", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "user_id")
    private Usuario users; //Muchas listas de to-dos pueden pertenecer a un usuario

    public ListTodo(String list_todo_id, String name) {
        this.list_todo_id = list_todo_id;
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

    public String getList_todo_id() {
        return list_todo_id;
    }
}
