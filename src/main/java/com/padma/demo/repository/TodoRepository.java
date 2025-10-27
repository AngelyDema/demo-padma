package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String> {
    List<Todo> findAllByUsersAndCompletedFalse(String users); //Por usuario y si no está "done"
    List<Todo> findAllByUsersAndCompletedTrue(String users);
    
}

