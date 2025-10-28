package com.padma.demo.models;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "todos")
public class Todo {
	
    //Atributos
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="todo_id", nullable = false)
    private Long todoId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users; //Muchos to-dos pueden pertenecer a un User

    @ManyToOne
    @JoinColumn(name = "list_todo_id")
    private ListTodo listTodos; //Muchos to-dos pueden pertenecer a una lista de to-dos

    @Column(name="name", nullable = false)
    private String name;

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

    
}
