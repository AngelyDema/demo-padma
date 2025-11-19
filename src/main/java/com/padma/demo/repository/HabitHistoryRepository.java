package com.padma.demo.repository;

import com.padma.demo.models.HabitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitHistoryRepository extends JpaRepository<HabitHistory, Long> {

    // ✅ Buscar por ID del hábito (Long)
    List<HabitHistory> findByHabitHabitId(Long habitId);

    // ✅ Buscar por objeto Habit
    Optional<HabitHistory> findByHabit_HabitId(Long habitId);
}
