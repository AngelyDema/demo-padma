package com.padma.demo.repository;

import java.util.List;
import com.padma.demo.models.ListTodo;
import com.padma.demo.models.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ListTodoRepository extends JpaRepository<ListTodo, Long> {

    List<ListTodo> findByUsers(Long users);

    Optional<ListTodo> findById(Long id);

    List<ListTodo> findAllByUsers_UserId(Long userId);

    List<ListTodo> findByNameContainingIgnoreCase(String name);
}
