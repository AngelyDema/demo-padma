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

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
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

        habit.setCompleted(false); // por defecto no está completado al crear

        return habitRepository.save(habit);
    }
}
