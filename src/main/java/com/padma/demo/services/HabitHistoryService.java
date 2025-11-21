package com.padma.demo.services;

import com.padma.demo.models.Habit;
import com.padma.demo.models.HabitHistory;
import com.padma.demo.repository.HabitHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections; // ✅ ADDED THIS IMPORT

import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HabitHistoryService {

    private final HabitHistoryRepository habitHistoryRepository;

    public HabitHistoryService(HabitHistoryRepository habitHistoryRepository) {
        this.habitHistoryRepository = habitHistoryRepository;
    }

    // ✅ Actualizar racha
    // ✅ FIXED: Update streak with duplicate prevention
    public HabitHistory updateStreak(Habit habit) {
        log.info("📊 Actualizando racha para hábito: {}", habit.getTitle());

        HabitHistory history = habitHistoryRepository
                .findByHabit_HabitId(habit.getHabitId())
                .orElseGet(() -> {
                    log.info("📝 Creando nuevo HabitHistory");
                    HabitHistory newHistory = new HabitHistory();
                    newHistory.setHabit(habit);
                    newHistory.setCurrentStreak(0);
                    newHistory.setLongestStreak(0);
                    newHistory.setCompletionDates(new ArrayList<>());
                    return habitHistoryRepository.save(newHistory);
                });

        LocalDate today = LocalDate.now();

        // ✅ CRITICAL: Prevent duplicate completion
        if (history.getCompletionDates() != null && history.getCompletionDates().contains(today)) {
            log.warn("⚠️ Hábito ya completado hoy - no se incrementa racha");
            throw new RuntimeException("Este hábito ya fue completado hoy");
        }

        LocalDate yesterday = today.minusDays(1);
        boolean completedYesterday = history.getCompletionDates() != null &&
                history.getCompletionDates().contains(yesterday);

        // ✅ Actualizar racha
        if (completedYesterday) {
            history.setCurrentStreak(history.getCurrentStreak() + 1);
            log.info("✅ Racha aumentada a: {}", history.getCurrentStreak());
        } else {
            history.setCurrentStreak(1);
            log.info("🔄 Nueva racha iniciada");
        }

        // ✅ Actualizar racha más larga
        if (history.getCurrentStreak() > history.getLongestStreak()) {
            history.setLongestStreak(history.getCurrentStreak());
            log.info("🏆 Racha más larga actualizada a: {}", history.getLongestStreak());
        }

        // ✅ Agregar fecha a completadas
        if (history.getCompletionDates() == null) {
            history.setCompletionDates(new ArrayList<>());
        }
        history.getCompletionDates().add(today);

        return habitHistoryRepository.save(history);
    }

    // ✅ FIXED: Properly handle same-day uncheck
    public HabitHistory updateStreakOnUncompleted(Habit habit) {
        log.info("📊 Actualizando racha (DESCOMPLETADO) para hábito: {}", habit.getTitle());

        HabitHistory history = habitHistoryRepository
                .findByHabit_HabitId(habit.getHabitId())
                .orElseThrow(() -> new RuntimeException("HabitHistory no encontrado"));

        LocalDate today = LocalDate.now();

        // ✅ Remove today's date
        if (history.getCompletionDates() != null) {
            boolean removed = history.getCompletionDates().remove(today);

            if (!removed) {
                log.warn("⚠️ La fecha de hoy no estaba en completadas");
                return history;
            }

            log.info("🗑️ Fecha de hoy eliminada de completadas");

            // ✅ FIXED: Recalculate streak properly
            LocalDate yesterday = today.minusDays(1);

            if (history.getCompletionDates().isEmpty()) {
                // No completions left
                history.setCurrentStreak(0);
                log.info("🔄 Racha reiniciada a 0 (sin completados)");
            } else if (history.getCompletionDates().contains(yesterday)) {
                // Yesterday was completed, so streak continues from yesterday
                history.setCurrentStreak(Math.max(0, history.getCurrentStreak() - 1));
                log.info("📉 Racha decrementada a: {}", history.getCurrentStreak());
            } else {
                // No continuity - need to recalculate streak from scratch
                int newStreak = calculateCurrentStreak(history.getCompletionDates());
                history.setCurrentStreak(newStreak);
                log.info("🔄 Racha recalculada: {}", newStreak);
            }
        }

        return habitHistoryRepository.save(history);
    }

    // ✅ NEW: Public method to recalculate and save streak on page load
    public HabitHistory recalculateStreakOnPageLoad(Habit habit) {
        HabitHistory history = habitHistoryRepository
                .findByHabit_HabitId(habit.getHabitId())
                .orElse(null);

        if (history == null) {
            return null;
        }

        int correctStreak = calculateCurrentStreak(history.getCompletionDates());

        // Only update if streak is different
        if (history.getCurrentStreak() != correctStreak) {
            log.info("🔄 Recalculando racha para hábito {}: {} → {}",
                    habit.getTitle(), history.getCurrentStreak(), correctStreak);
            history.setCurrentStreak(correctStreak);
            return habitHistoryRepository.save(history);
        }

        return history;
    }

    // ✅ NEW: Helper method to recalculate streak from completion dates
    private int calculateCurrentStreak(List<LocalDate> completionDates) {
        if (completionDates == null || completionDates.isEmpty()) {
            return 0;
        }

        // Sort dates descending
        List<LocalDate> sorted = new ArrayList<>(completionDates);
        sorted.sort(Collections.reverseOrder());

        LocalDate mostRecent = sorted.get(0);
        LocalDate today = LocalDate.now();

        // If most recent is not today or yesterday, streak is broken
        if (mostRecent.isBefore(today.minusDays(1))) {
            return 0;
        }

        // Count consecutive days
        int streak = 0;
        LocalDate expectedDate = today.minusDays(1); // Start from yesterday

        for (LocalDate date : sorted) {
            if (date.equals(expectedDate)) {
                streak++;
                expectedDate = expectedDate.minusDays(1);
            } else if (date.isBefore(expectedDate)) {
                break;
            }
        }

        return streak;
    }
}
