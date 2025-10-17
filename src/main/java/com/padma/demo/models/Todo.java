package com.padma.demo.models;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "todos")
public class Todo {
	
    //Atributos
    
    @Id
    private String todo_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario users; //Muchos to-dos pueden pertenecer a un usuario

    @ManyToOne
    @JoinColumn(name = "list_todo_id")
    private ListTodo list_todos; //Muchos to-dos pueden pertenecer a una lista de to-dos

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="description")
    private String description;

    @Column(name="completed")
    private boolean completed;

    @Column(name="priority")
    private String priority;

    @Column(name="due_date")
    private LocalDate dueDate;

    @Column(name="created_at", nullable = false)
    private LocalDate createdAt = LocalDate.now();
    
    @Column(name="completed_at")
    private LocalDate completedAt;

    //Constructor
    protected Todo() {}

    public Todo(String todo_id, Usuario user, String title, String description, String priority, LocalDate dueDate, boolean completed) {
        this.todo_id = todo_id;
        this.users = user;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
    }



    //Getters y Setters
    public String getTodo_Id() {
        return todo_id;
    }
    public void setId(String todo_id) {
        this.todo_id = todo_id;
    }
    public Usuario getUser() {
        return users;
    }
    public void setUser(Usuario user) {
        this.users = user;
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

