package com.padma.demo.controllers;

import com.padma.demo.models.Todo;
import com.padma.demo.services.TodoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
    public ResponseEntity<?> updateTodo(
            @PathVariable Long todoId,
            @RequestBody Todo updatedTodo) {

        log.info("==> PUT /api/todos/id/{}", todoId);
        log.debug("📋 Payload recibido: {}", updatedTodo);

        try {
            Todo updated = todoService.updateTodo(todoId, updatedTodo);
            log.info("✅ Todo actualizado: {}", updated.getName());

            // ✅ Devuelve un JSON simple (no la entidad completa)
            Map<String, Object> response = new HashMap<>();
            response.put("todoId", updated.getTodoId());
            response.put("name", updated.getName());
            response.put("description", updated.getDescription());
            response.put("dueDate", updated.getDueDate());
            response.put("priority", updated.getPriority());

            if (updated.getListTodos() != null) {
                response.put("listTodoId", updated.getListTodos().getListTodoId());
            }

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            log.error("❌ Error actualizando todo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
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