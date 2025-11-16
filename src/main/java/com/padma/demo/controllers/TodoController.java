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

    // Crear Todo
    @PostMapping("/create")
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
    @PutMapping("/id/{todoId}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long todoId, @RequestBody Todo updatedTodo) {
        Todo updated = todoService.updateTodo(todoId, updatedTodo);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    // Eliminar un ToDo
    @DeleteMapping("/id/{todoId}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long todoId) {
        todoService.deleteTodo(todoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // marcar como completo
    @PutMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleTodo(@PathVariable Long id) {
        try {
            todoService.toggleCompletion(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}