package com.padma.demo.services;

import org.springframework.stereotype.Service;
import com.padma.demo.models.Habit;
import com.padma.demo.repository.HabitRepository;
import com.padma.demo.models.User;
import com.padma.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import com.padma.demo.models.Area;
import com.padma.demo.repository.AreaRepository;
import com.padma.demo.models.HabitHistory;
import com.padma.demo.repository.HabitHistoryRepository;
import com.padma.demo.services.HabitHistoryService;
import com.padma.demo.services.HabitCompletionService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitHistoryRepository habitHistoryRepository; // ✅ ADDED THIS
    private final HabitHistoryService habitHistoryService;
    private final HabitCompletionService habitCompletionService;

    // ✅ UPDATED CONSTRUCTOR - Added habitHistoryRepository parameter
    public HabitService(HabitRepository habitRepository,
            UserRepository userRepository,
            HabitHistoryRepository habitHistoryRepository, // ✅ ADDED THIS PARAMETER
            HabitHistoryService habitHistoryService,
            HabitCompletionService habitCompletionService) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.habitHistoryRepository = habitHistoryRepository; // ✅ ADDED THIS
        this.habitHistoryService = habitHistoryService;
        this.habitCompletionService = habitCompletionService;
    }

    public List<Habit> getHabitsByUserId(Long userId) {
        return habitRepository.findAllByUsers_UserId(userId);
    }

    // GET HABITS BY AREA AND USER
    public List<Habit> getHabitsByAreaAndUser(Long areaId, Long userId) {
        log.debug("🔍 Obteniendo hábitos del área {} para el usuario {}", areaId, userId);
        return habitRepository.findAllByAreas_AreaIdAndUsers_UserId(areaId, userId);
    }

    // Crear un hábito
    @Transactional
    public Habit createHabit(Habit habit) {

        // --- 1) resolver siempre el User ---
        User user = habit.getUsers();
        if (user == null || user.getUserId() == null) {
            throw new RuntimeException("User information is missing in Habit or userId is null");
        }
        Long userId = user.getUserId();
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        habit.setUsers(existingUser); // ahora es managed y tiene todos los campos

        if (habit.getTitle() == null) {
            habit.setTitle("Habit without name");
        }

        if (habit.getDescription() == null) {
            habit.setDescription("Habit without description");
        }

        if (habit.getCreatedAt() == null) {
            habit.setCreatedAt(java.time.LocalDate.now());
        }

        habit.setCompleted(false); // por defecto no está completado al crear

        return habitRepository.save(habit);
    }

    // Marcar hábito como completado (con nota)
    public Habit markHabitAsCompleted(Long habitId, String note) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado con ID: " + habitId));

        if (note == null || note.trim().isEmpty()) {
            throw new RuntimeException("Es obligatorio añadir una nota al completar el hábito.");
        }

        // ✅ NEW: Check if already completed today
        LocalDate today = LocalDate.now();
        HabitHistory history = habitHistoryRepository
                .findByHabit_HabitId(habitId)
                .orElse(null);

        if (history != null && history.getCompletionDates() != null
                && history.getCompletionDates().contains(today)) {
            throw new RuntimeException("Este hábito ya fue completado hoy");
        }

        habit.setCompleted(true);
        history = habitHistoryService.updateStreak(habit);
        habitCompletionService.createCompletion(history, note);

        return habitRepository.save(habit);
    }

    // Unmark with note deletion
    public Habit unmarkHabitAsCompleted(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado con ID: " + habitId));

        LocalDate today = LocalDate.now();

        // Verify it was completed today
        HabitHistory history = habitHistoryRepository
                .findByHabit_HabitId(habitId)
                .orElseThrow(() -> new RuntimeException("HabitHistory no encontrado"));

        if (history.getCompletionDates() == null
                || !history.getCompletionDates().contains(today)) {
            throw new RuntimeException("Este hábito no fue completado hoy");
        }

        // Delete today's completion note
        habitCompletionService.deleteTodayCompletion(history.getHabitHistoryId());

        // Update streak
        habitHistoryService.updateStreakOnUncompleted(habit);

        habit.setCompleted(false);
        return habitRepository.save(habit);
    }

    // Also recalculates streaks on page load
    public void resetDailyCompletionFlags(Long userId) {
        List<Habit> habits = habitRepository.findAllByUsers_UserId(userId);
        LocalDate today = LocalDate.now();

        habits.forEach(habit -> {
            HabitHistory history = habit.getHabitHistory();

            // Only keep checked if completed TODAY
            boolean completedToday = history != null
                    && history.getCompletionDates() != null
                    && history.getCompletionDates().contains(today);

            if (!completedToday && habit.isCompleted()) {
                habit.setCompleted(false);
                habitRepository.save(habit);
            }

            // Recalculate streak on every page load
            habitHistoryService.recalculateStreakOnPageLoad(habit);
        });
    }

    // Actualizar un hábito existente
    public Habit updateHabit(Long habitId, Map<String, Object> data) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado con ID: " + habitId));

        // Actualizar campos si están presentes en el payload
        if (data.containsKey("title")) {
            Object v = data.get("title");
            if (v != null)
                habit.setTitle(v.toString());
        }
        if (data.containsKey("description")) {
            Object v = data.get("description");
            habit.setDescription(v != null ? v.toString() : null);
        }
        if (data.containsKey("goal")) {
            Object v = data.get("goal");
            if (v != null) {
                int g = Integer.parseInt(v.toString());
                habit.setGoal(g);
            }
        }
        if (data.containsKey("areaId")) {
            Object v = data.get("areaId");
            if (v != null) {
                Long areaId = Long.valueOf(v.toString());
                Area area = new Area();
                area.setAreaId(areaId);
                habit.setAreas(area);
            } else {
                habit.setAreas(null);
            }
        }
        // Si quieres permitir cambiar el estado de completado o fecha, añade aquí:
        if (data.containsKey("completed")) {
            Object v = data.get("completed");
            if (v != null)
                habit.setCompleted(Boolean.parseBoolean(v.toString()));
        }

        // Persistir cambios
        return habitRepository.save(habit);
    }

    // ✅ Eliminar un hábito
    public void deleteHabit(Long habitId) {
        Habit existingHabit = habitRepository.findByHabitId(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found with ID: " + habitId));
        habitRepository.delete(existingHabit);
    }

}
