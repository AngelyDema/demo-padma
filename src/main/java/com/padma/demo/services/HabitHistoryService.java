package com.padma.demo.services;

import com.padma.demo.models.Habit;
import com.padma.demo.models.HabitHistory;
import com.padma.demo.repository.HabitHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HabitHistoryService {

    private final HabitHistoryRepository habitHistoryRepository;

    public HabitHistoryService(HabitHistoryRepository habitHistoryRepository) {
        this.habitHistoryRepository = habitHistoryRepository;
    }

    // Actualizar o crear registro de racha
    public HabitHistory updateStreak(Habit habit) {
        HabitHistory history = habitHistoryRepository.findByHabit(habit.getHabitId())
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    HabitHistory newHistory = new HabitHistory();
                    newHistory.setHabit(habit);
                    newHistory.setCurrentStreak(0);
                    newHistory.setLongestStreak(0);
                    newHistory.setCompletionDates(new java.util.ArrayList<>());
                    return habitHistoryRepository.save(newHistory);
                });

        LocalDate today = LocalDate.now();
        if (history.getCompletionDates().contains(today))
            return history;

        LocalDate yesterday = today.minusDays(1);
        boolean completedYesterday = history.getCompletionDates().contains(yesterday);

        if (completedYesterday) {
            history.setCurrentStreak(history.getCurrentStreak() + 1);
        } else {
            history.setCurrentStreak(1);
        }

        if (history.getCurrentStreak() > history.getLongestStreak()) {
            history.setLongestStreak(history.getCurrentStreak());
        }

        history.getCompletionDates().add(today);
        return habitHistoryRepository.save(history);
    }
}
