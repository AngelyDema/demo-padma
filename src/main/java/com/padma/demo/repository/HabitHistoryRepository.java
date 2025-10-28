package com.padma.demo.repository;

import com.padma.demo.models.HabitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HabitHistoryRepository extends JpaRepository<HabitHistory, Long> {

    List<HabitHistory> findByHabit(Long habit);

    Optional<HabitHistory> findByHabitAndCompletionDates(Long habit, LocalDate completionDates);
}
