package com.padma.demo.repository;
import com.padma.demo.models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, String> {

    List<Habit> findAllByUserId(String user_id);  //Encontrar referencias por id de user
    List<Habit> findAllByAreaHabitId( String area_id); //Lo mismo pero por área

}