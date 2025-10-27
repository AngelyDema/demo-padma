package com.padma.demo.repository;

import java.util.List;
import com.padma.demo.models.ListTodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListTodoRepository extends JpaRepository<ListTodo, String> {

    List<ListTodo> findByUserId(String users);
    List<ListTodo> findByTituloContainingIgnoreCase(String name);
}
