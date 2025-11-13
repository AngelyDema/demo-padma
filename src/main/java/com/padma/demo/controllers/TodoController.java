package com.padma.demo.controllers;

import com.padma.demo.models.Todo;
import com.padma.demo.services.TodoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    // Constructor-based injection
    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // Obtener todos los ToDos de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Todo>> getTodosByUser(@PathVariable Long userId) {
        List<Todo> todos = todoService.getTodosByUser(userId);
        return new ResponseEntity<>(todos, HttpStatus.OK);
    }

    // Crear Todo
    @PostMapping("/")
    public ResponseEntity<?> createTodo(@RequestBody Todo todo, @RequestParam(required = false) Long listTodoId) {
        try {
            Todo createTodo = todoService.createTodo(todo, listTodoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createTodo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Actualizar un ToDo
    @PutMapping("/{todoId}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long todoId, @RequestBody Todo updatedTodo) {
        Todo updated = todoService.updateTodo(todoId, updatedTodo);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar un ToDo
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long todoId) {
        todoService.deleteTodo(todoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}