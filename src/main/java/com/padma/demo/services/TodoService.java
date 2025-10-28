package com.padma.demo.services;

import org.springframework.stereotype.Service;
import com.padma.demo.models.Todo;
import com.padma.demo.repository.TodoRepository;
import java.util.List;
import java.util.Optional;
import com.padma.demo.models.ListTodo;
import com.padma.demo.repository.ListTodoRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final ListTodoRepository listTodoRepository;

    public TodoService(TodoRepository todoRepository, ListTodoRepository listTodoRepository) {
        this.todoRepository = todoRepository;
        this.listTodoRepository = listTodoRepository;
    }

    // Crud Todo

    // crear todo

    public Todo createTodo(Todo todos, Long listTodoId) {

        if (listTodoId != null) {
            Optional<ListTodo> listOptional = listTodoRepository.findById(listTodoId);
            if (listOptional.isPresent()) {
                ListTodo listTodos = listOptional.get();
                todos.setListTodos(listTodos); // asocia el todo a la lista
                todos.setUsers(listTodos.getUsers()); // asigna el usuario de la lista al todo
            } else {
                throw new RuntimeException("No se encontró la lista con ID: " + listTodoId);
            }
        }

        // Validación de prioridad (matrix de este man eisenhower )
        String priority = todos.getPriority();
        if (priority == null || !isValidPriority(priority)) {
            todos.setPriority("Not Urgent but Important"); // Valor por defecto
        }

        // Fecha límite por defecto
        if (todos.getDueDate() == null) {
            todos.setDueDate(LocalDate.now().plusDays(1)); // Por defecto, mañana
        }

        // Estados iniciales
        todos.setCompleted(false);
        todos.setCompletedAt(null);
        todos.setCreatedAt(LocalDate.now());

        return todoRepository.save(todos);
    }

    // Editar Todo
    public Todo updateTodo(Long todoId, Todo updatedTodo) {
        Todo existingTodo = todoRepository.findByTodoId(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found with ID: " + todoId));
        existingTodo.setName(updatedTodo.getName());
        existingTodo.setDescription(updatedTodo.getDescription());
        existingTodo.setDueDate(updatedTodo.getDueDate());
        existingTodo.setPriority(updatedTodo.getPriority());
        return todoRepository.save(existingTodo);
    }

    // Eliminar Todo
    public void deleteTodo(Long todoId) {
        Todo existingTodo = todoRepository.findByTodoId(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found with ID: " + todoId));
        todoRepository.delete(existingTodo);
    }

    // obtener todo por id
    public Todo getTodoByTodoId(Long todoId) {
        return todoRepository.findAllByTodoId(todoId)
                .orElseThrow(() -> new RuntimeException("Todo no encontrado"));
    }

    private boolean isValidPriority(String priority) {
        return priority.equalsIgnoreCase("Urgent and Important")
                || priority.equalsIgnoreCase("Urgent but not Important")
                || priority.equalsIgnoreCase("Not Urgent but Important")
                || priority.equalsIgnoreCase("Not Urgent and Not Important");
    }

}
