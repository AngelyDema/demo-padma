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
import com.padma.demo.repository.UserRepository;
import com.padma.demo.models.User;
import jakarta.transaction.Transactional;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final ListTodoRepository listTodoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, ListTodoRepository listTodoRepository,
            UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.listTodoRepository = listTodoRepository;
        this.userRepository = userRepository;
    }

    // Crud Todo

    // crear todo

    @Transactional
    public Todo createTodo(Todo todos, Long listTodoId) {
        if (todos == null) {
            throw new RuntimeException("Todo is null");
        }

        // --- 1) resolver siempre el User ---
        User user = todos.getUsers();
        if (user == null || user.getUserId() == null) {
            throw new RuntimeException("User information is missing in Todo or userId is null");
        }
        Long userId = user.getUserId();
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        todos.setUsers(existingUser); // ahora es managed y tiene todos los campos

        // --- 2) asociar lista si viene ---
        if (listTodoId != null) {
            Optional<ListTodo> listOptional = listTodoRepository.findById(listTodoId);
            if (listOptional.isPresent()) {
                todos.setListTodos(listOptional.get());
            } else {
                throw new RuntimeException("No se encontró la lista con ID: " + listTodoId);
            }
        }

        // --- 3) defaults y validaciones ---
        String priority = todos.getPriority();
        if (priority == null || !isValidPriority(priority)) {
            todos.setPriority("Not Urgent but Important");
        }
        if (todos.getDueDate() == null) {
            todos.setDueDate(LocalDate.now().plusDays(1));
        }
        todos.setCompleted(false);
        todos.setCompletedAt(null);
        todos.setCreatedAt(LocalDate.now());

        // --- 4) guardar y devolver ---
        Todo saved = todoRepository.save(todos);

        // saved.getUsers() es el existingUser (con campos cargados)
        return saved;
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

    // obtener por User
    public List<Todo> getTodosByUser(Long userId) {
        return todoRepository.findByUsers(userId);
    }

}
