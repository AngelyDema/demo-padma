package com.padma.demo.controllers;

import com.padma.demo.models.Habit;
import com.padma.demo.services.HabitService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.padma.demo.models.User;
import com.padma.demo.models.HabitHistory;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping("/createHabit")
    public ResponseEntity<?> createHabit(@RequestBody Map<String, Object> request) {
        try {
            // Extraer los campos del JSON
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            int goal = request.get("goal") != null ? Integer.parseInt(request.get("goal").toString()) : 0;

            // Crear instancias de los modelos
            User user = new User();
            user.setUserId(userId);

            Habit habit = new Habit();
            habit.setUsers(user);
            habit.setTitle(title);
            habit.setDescription(description);
            habit.setGoal(goal);

            // Llamar al service
            Habit createdHabit = habitService.createHabit(habit);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHabit);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{habitId}/complete")
    public ResponseEntity<?> markHabitAsCompleted(@PathVariable Long habitId, @RequestBody Map<String, String> body) {
        try {
            String note = body.get("note");
            Habit updatedHabit = habitService.markHabitAsCompleted(habitId, note);
            return ResponseEntity.ok(updatedHabit);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getHabitsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(habitService.getHabitsByUser(userId));
    }
}
