package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {

    List<Todo> findbyAllTodoList(String list_todo_id); //Todos por id de listas
    List<Todo> findbyAllTodo(String todo_id); //todos los todos
    List<Todo> findAllByUserIdAndCompletedFalse(String user_id); //Por usuario y si no está "done"
    List<Todo> findAllByUserIdAndCompleteTrue(String user_id);
    
}

