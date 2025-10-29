package com.padma.demo.repository;

import com.padma.demo.models.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.padma.demo.models.HabitHistory;
import java.util.List;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    List<HabitCompletion> findByHabitHistories_HabitHistoryId(Long habitHistoryId);
}
