package com.padma.demo.controllers;

import org.springframework.web.bind.annotation.*;
import com.padma.demo.services.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.padma.demo.models.ListTodo;
import com.padma.demo.models.Todo;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/lists")
public class ListTodoController {

    @Autowired
    private ListService listService;

    @Autowired
    public ListTodoController(ListService listService) {
        this.listService = listService;
    }

    // Crear Lista
    @PostMapping("/createList")
    public ResponseEntity<?> createListTodo(@RequestBody ListTodo listTodo) {
        try {
            ListTodo createdList = listService.createList(listTodo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/id/{listId}")
    public ResponseEntity<?> updateList(
            @PathVariable Long listId,
            @RequestBody ListTodo updatedList) {

        log.info("==> PUT /api/lists/id/{}", listId);
        log.debug("📋 Payload recibido: {}", updatedList);

        try {
            ListTodo updated = listService.updateList(listId, updatedList);
            log.info("✅ Lista actualizada: {}", updated.getName());

            Map<String, Object> response = new HashMap<>();
            response.put("listTodoId", updated.getListTodoId());
            response.put("name", updated.getName());
            response.put("description", updated.getDescription());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            log.error("❌ Error actualizando lista: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/id/{listId}")
    public ResponseEntity<?> deleteList(@PathVariable Long listId) {
        log.info("==> DELETE /api/lists/id/{}", listId);

        try {
            listService.deleteList(listId);
            log.info("✅ Lista eliminada");
            return ResponseEntity.ok(Map.of("message", "Lista eliminada exitosamente"));
        } catch (RuntimeException e) {
            log.error("❌ Error eliminando lista: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
