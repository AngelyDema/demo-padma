package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {

    List<Todo> findbyAllTodoList(String list_todos); //Todos por id de listas
    List<Todo> findbyAllTodo(String todos); //todos los todos
    List<Todo> findAllByUser_idAndCompletedFalse(String users); //Por usuario y si no está "done"
    List<Todo> findAllByUser_idAndCompleteTrue(String users);
    
}

