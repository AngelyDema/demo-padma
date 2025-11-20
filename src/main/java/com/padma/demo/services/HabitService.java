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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitHistoryService habitHistoryService;
    private final HabitCompletionService habitCompletionService;

    public HabitService(HabitRepository habitRepository, UserRepository userRepository,
            HabitHistoryService habitHistoryService, HabitCompletionService habitCompletionService) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.habitHistoryService = habitHistoryService;
        this.habitCompletionService = habitCompletionService;
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

        if (note == null || note.isEmpty()) {
            throw new RuntimeException("Es obligatorio añadir una nota al completar el hábito.");
        }

        habit.setCompleted(true);
        HabitHistory history = habitHistoryService.updateStreak(habit);
        habitCompletionService.createCompletion(history, note);

        return habitRepository.save(habit);
    }

    // NUEVO MÉTODO para desmarcar hábito
    @Transactional
    public Habit unmarkHabitAsCompleted(Long habitId) {
        log.info("❌ Desmarcando hábito: {}", habitId);

        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado con ID: " + habitId));

        // Desmarcar
        habit.setCompleted(false);

        // Actualizar streak (decrement logic)
        habitHistoryService.updateStreakOnUncompleted(habit);

        return habitRepository.save(habit);
    }

    // Obtener todos los hábitos de un usuario
    public List<Habit> getHabitsByUserId(Long userId) {
        return habitRepository.findAllByUsers_UserId(userId);
    }

    // Calcular progreso (% hacia goal)
    public int calculateProgress(Habit habit) {
        int streak = habit.getHabitHistory().getCurrentStreak();
        int goal = habit.getGoal();
        return (int) ((double) streak / goal * 100);
    }
}
