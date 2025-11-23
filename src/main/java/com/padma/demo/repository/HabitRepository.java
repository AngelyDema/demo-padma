package com.padma.demo.repository;

import com.padma.demo.models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findAllByUsers_UserId(Long userId); // Encontrar referencias por id de user

    List<Habit> findAllByAreas(Long areas); // Lo mismo pero por área

    List<Habit> findAllByAreas_AreaIdAndUsers_UserId(Long areaId, Long userId);

    Optional<Habit> findByHabitId(Long habitId);

}