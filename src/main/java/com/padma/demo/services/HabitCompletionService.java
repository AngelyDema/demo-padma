package com.padma.demo.services;

import com.padma.demo.models.HabitCompletion;
import com.padma.demo.models.HabitHistory;
import com.padma.demo.repository.HabitCompletionRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HabitCompletionService {

    private final HabitCompletionRepository habitCompletionRepository;

    public HabitCompletionService(HabitCompletionRepository habitCompletionRepository) {
        this.habitCompletionRepository = habitCompletionRepository;
    }

    // Crear un nuevo registro de completado
    public HabitCompletion createCompletion(HabitHistory history, String note) {
        HabitCompletion completion = new HabitCompletion();
        completion.setHabitHistories(history);
        completion.setNote(note);
        completion.setDateCompleted(LocalDate.now());
        return habitCompletionRepository.save(completion);
    }

    // Obtener historial de completados por hábito
    public List<HabitCompletion> getCompletionsByHistory(Long habitHistoryId) {
        return habitCompletionRepository.findByHabitHistories_HabitHistoryId(habitHistoryId);
    }

    // ✅ NEW: Delete today's completion when unchecking
    public void deleteTodayCompletion(Long habitHistoryId) {
        LocalDate today = LocalDate.now();
        List<HabitCompletion> completions = habitCompletionRepository
                .findByHabitHistories_HabitHistoryId(habitHistoryId);

        completions.stream()
                .filter(c -> c.getDateCompleted().equals(today))
                .forEach(habitCompletionRepository::delete);
    }
}
