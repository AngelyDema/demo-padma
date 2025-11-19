package com.padma.demo.controllers;

import com.padma.demo.models.Habit;
import com.padma.demo.models.User;
import com.padma.demo.models.Area;
import com.padma.demo.services.HabitService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.padma.demo.repository.HabitRepository;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    // Obtener todos los hábitos de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Habit>> getHabitsByUserId(@PathVariable Long userId) {
        List<Habit> habits = habitService.getHabitsByUserId(userId);
        return new ResponseEntity<>(habits, HttpStatus.OK);
    }

    @PostMapping("/createHabit")
    public ResponseEntity<?> createHabit(@RequestBody Map<String, Object> request) {
        log.info("==> POST /api/habits/createHabit");
        log.debug("📋 Payload recibido: {}", request);

        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = (String) request.get("title");
            String description = (String) request.get("description");
            int goal = request.get("goal") != null ? Integer.parseInt(request.get("goal").toString()) : 30;
            boolean completed = request.get("completed") != null
                    ? Boolean.parseBoolean(request.get("completed").toString())
                    : false;

            User user = new User();
            user.setUserId(userId);

            Habit habit = new Habit();
            habit.setUsers(user);
            habit.setTitle(title);
            habit.setDescription(description);
            habit.setGoal(goal);
            habit.setCompleted(completed);

            // ✅ AGREGAR ÁREA SI VIENE EN EL PAYLOAD
            if (request.containsKey("areaId") && request.get("areaId") != null) {
                Long areaId = Long.valueOf(request.get("areaId").toString());
                Area area = new Area();
                area.setAreaId(areaId);
                habit.setAreas(area);
                log.info("✅ Área asignada al hábito: {}", areaId);
            }

            Habit createdHabit = habitService.createHabit(habit);
            log.info("✅ Hábito creado: {}", createdHabit.getTitle());

            return ResponseEntity.status(HttpStatus.CREATED).body(createdHabit);

        } catch (Exception e) {
            log.error("❌ Error creando hábito: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{habitId}/complete")
    public ResponseEntity<?> markHabitAsCompleted(
            @PathVariable Long habitId,
            @RequestBody Map<String, Object> body) {

        log.info("==> POST /api/habits/{}/complete", habitId);

        try {
            String note = (String) body.get("note");

            if (note == null || note.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La nota es obligatoria"));
            }

            Habit updatedHabit = habitService.markHabitAsCompleted(habitId, note);

            // ✅ Devolver solo datos necesarios (sin relaciones circulares)
            Map<String, Object> response = new HashMap<>();
            response.put("habitId", updatedHabit.getHabitId());
            response.put("title", updatedHabit.getTitle());
            response.put("completed", updatedHabit.isCompleted());
            response.put("streak",
                    updatedHabit.getHabitHistory() != null ? updatedHabit.getHabitHistory().getCurrentStreak() : 0);
            response.put("longestStreak",
                    updatedHabit.getHabitHistory() != null ? updatedHabit.getHabitHistory().getLongestStreak() : 0);

            log.info("✅ Hábito completado: {}", updatedHabit.getTitle());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("❌ Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
