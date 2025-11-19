package com.padma.demo.services;

import java.util.List;
import java.util.ArrayList;
import org.springframework.stereotype.Service;
import com.padma.demo.models.ListTodo;
import com.padma.demo.repository.ListTodoRepository;
import com.padma.demo.repository.TodoRepository;
import com.padma.demo.models.Todo;
import com.padma.demo.models.User;
import jakarta.transaction.Transactional;
import com.padma.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ListService {

    ListTodoRepository listTodoRepository;
    UserRepository userRepository;
    TodoRepository todoRepository;

    public ListService(ListTodoRepository listTodoRepository, UserRepository userRepository,
            TodoRepository todoRepository) {
        this.listTodoRepository = listTodoRepository;
        this.userRepository = userRepository;
        this.todoRepository = todoRepository;
    }

    public ListTodo getListById(Long id) {
        log.info("🔍 Buscando ListTodo con ID: {}", id);

        var result = listTodoRepository.findByListTodoId(id);
        if (result.isEmpty()) {
            log.error("❌ No se encontró lista con ID: {}", id);
            throw new RuntimeException("Lista no encontrada con ID: " + id);
        }

        log.info("✅ Lista encontrada: {}", result.get().getName());
        return result.get();
    }

    public List<Todo> getTodosByList(Long listId) {
        log.info("🔍 Buscando todos para lista ID: {}", listId);

        try {
            List<Todo> todos = todoRepository.findAllByListTodos_ListTodoId(listId);
            log.info("✅ {} todos encontrados", todos.size());
            return todos;
        } catch (Exception e) {
            log.error("❌ Error en getTodosByList: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener tareas: " + e.getMessage());
        }
    }

    // Crear una lista
    @Transactional
    public ListTodo createList(ListTodo listTodo) {

        // --- 1) resolver siempre el User ---
        User user = listTodo.getUsers();
        if (user == null || user.getUserId() == null) {
            throw new RuntimeException("User information is missing in Todo or userId is null");
        }
        Long userId = user.getUserId();
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        listTodo.setUsers(existingUser); // ahora es managed y tiene todos los campos

        if (listTodo.getName() == null) {
            listTodo.setName("List without name");
        }

        if (listTodo.getDescription() == null) {
            listTodo.setDescription("List without description");
        }

        if (listTodo.getTodos() == null) {
            listTodo.setTodos(new ArrayList<Todo>());
        }

        return listTodoRepository.save(listTodo);
    }

    // Editar lista
    public ListTodo updateList(Long id, ListTodo updatedList) {
        ListTodo existing = listTodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ListTodo not found"));
        existing.setName(updatedList.getName());
        existing.setDescription(updatedList.getDescription());
        return listTodoRepository.save(existing);
    }

    // Eliminar lista
    public void deleteList(Long id) {
        listTodoRepository.deleteById(id);
    }

    public List<ListTodo> getListsByUser(Long userId) {
        return listTodoRepository.findAllByUsers_UserId(userId);
    }
}
