package com.padma.demo.services;

import com.padma.demo.models.Habit;
import com.padma.demo.models.HabitHistory;
import com.padma.demo.repository.HabitHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
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
    public HabitHistory updateStreak(Habit habit) {
        log.info("📊 Actualizando racha para hábito: {}", habit.getTitle());

        // Buscar o crear HabitHistory
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

        // ✅ Si ya fue completado hoy, no incrementes de nuevo
        if (history.getCompletionDates() != null && history.getCompletionDates().contains(today)) {
            log.warn("⚠️ Hábito ya completado hoy");
            return history;
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

    // Actualizar racha cuando se DESCOMPLETA (uncheck)
    public HabitHistory updateStreakOnUncompleted(Habit habit) {
        log.info("📊 Actualizando racha (DESCOMPLETADO) para hábito: {}", habit.getTitle());

        HabitHistory history = habitHistoryRepository
                .findByHabit_HabitId(habit.getHabitId())
                .orElseThrow(() -> new RuntimeException("HabitHistory no encontrado"));

        LocalDate today = LocalDate.now();

        // ✅ Eliminar la fecha de hoy de completadas
        if (history.getCompletionDates() != null) {
            history.getCompletionDates().remove(today);
            log.info("🗑️ Fecha de hoy eliminada de completadas");

            // ✅ Recalcular la racha
            LocalDate yesterday = today.minusDays(1);
            if (history.getCompletionDates().contains(yesterday)) {
                // Si ayer sí fue completado, la racha sigue siendo válida (pero no incrementa
                // hoy)
                log.info("ℹ️ Racha se mantiene porque ayer fue completado");
            } else {
                // Si ayer NO fue completado, resetea la racha
                history.setCurrentStreak(0);
                log.info("🔄 Racha reiniciada a 0 (no hay continuidad)");
            }
        }

        return habitHistoryRepository.save(history);
    }
}
