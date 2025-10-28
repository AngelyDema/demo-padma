package com.padma.demo.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "list_todos")

public class ListTodo {

    //Atributos

@Id
@Column(name="list_todo_id", nullable = false)
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long listTodoId;

    @Column(name="names", nullable = false)
    private String name;

    @Column(name="descriptions")
    private String description;

    // Agregación una lista puede contener muchas tareas
    @OneToMany(mappedBy = "listTodos", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users; //Muchas listas de to-dos pueden pertenecer a un usuario

}