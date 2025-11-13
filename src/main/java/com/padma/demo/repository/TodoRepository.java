package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Todo;
import com.padma.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUsersAndCompletedFalse(Long users); // Por usuario y si no está "done"

    Optional<Todo> findByTodoId(Long todoId);

    List<Todo> findAllByListTodos(Long listTodos);

    Optional<Todo> findAllByTodoId(Long todoId);

    List<Todo> findByUsers(Long users);

    List<Todo> findAllByUsersAndCompletedTrue(Long users);

}
