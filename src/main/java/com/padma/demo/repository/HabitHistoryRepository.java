package com.padma.demo.repository;

import com.padma.demo.models.HabitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitHistoryRepository extends JpaRepository<HabitHistory, String> {

    List<HabitHistory> findByHabit(String habits);

    Optional<HabitHistory> findByHabitAndCreatedDate(String habits, LocalDate createdAt);
    Optional<HabitHistory> findByHabitAndCompletionDate(String habits, LocalDate completionDates);
    }
