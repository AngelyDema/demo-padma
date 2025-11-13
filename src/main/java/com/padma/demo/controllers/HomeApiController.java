package com.padma.demo.controllers;

import com.padma.demo.models.User;
import com.padma.demo.services.HabitService;
import com.padma.demo.services.TodoService;
import com.padma.demo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/home")
public class HomeApiController {

    @Autowired
    private HabitService habitService;

    @Autowired
    private TodoService todoService;

    @GetMapping("/data")
    public Map<String, Object> getHomeData(HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");

        Map<String, Object> response = new HashMap<>();

        if (userId == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        response.put("todos", todoService.getTodosByUser(userId));
        response.put("habits", habitService.getHabitsByUserId(userId));

        return response;
    }
}
