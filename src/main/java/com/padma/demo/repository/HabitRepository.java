package com.padma.demo.repository;
import com.padma.demo.models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    List<Habit> findAllByUsers(Long users);  //Encontrar referencias por id de user
    List<Habit> findAllByAreas( Long areas); //Lo mismo pero por área

}