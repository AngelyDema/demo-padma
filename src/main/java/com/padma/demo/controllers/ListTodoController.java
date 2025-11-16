package com.padma.demo.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.padma.demo.services.ListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.padma.demo.models.ListTodo;
import com.padma.demo.models.Todo;
import java.util.Map;

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

}
