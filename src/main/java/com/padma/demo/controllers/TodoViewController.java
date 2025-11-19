package com.padma.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;

import com.padma.demo.models.Todo;
import com.padma.demo.models.ListTodo;
import com.padma.demo.services.TodoService;
import com.padma.demo.services.ListService;
import com.padma.demo.services.UserService;

@Slf4j
@Controller
@RequestMapping("/todos") // ✅ Base path específico
public class TodoViewController {

    private final UserService userService;
    private final TodoService todoService;
    private final ListService listService;

    public TodoViewController(UserService userService, TodoService todoService, ListService listService) {
        this.userService = userService;
        this.todoService = todoService;
        this.listService = listService;
    }

    // ✅ ESPECÍFICAS PRIMERO - Sin parámetros numéricos
    @GetMapping("/today")
    public String getTodayTodos(Model model, HttpSession session) {
        log.info("==> GET /todos/today");

        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null) {
            log.warn("Usuario no autenticado");
            return "redirect:/login";
        }

        List<Todo> todos = todoService.getTodosForToday(userId);
        List<ListTodo> lists = listService.getListsByUser(userId);

        log.info("✅ {} todos para la semana obtenidos", todos.size());

        model.addAttribute("userId", userId);
        model.addAttribute("todos", todos);
        model.addAttribute("lists", lists);
        model.addAttribute("selectedFilter", "Today");
        return "todos/todos";
    }

    @GetMapping("/week")
    public String getWeekTodos(Model model, HttpSession session) {
        log.info("==> GET /todos/week");

        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null) {
            log.warn("Usuario no autenticado");
            return "redirect:/login";
        }

        List<Todo> todos = todoService.getTodosForNext7Days(userId);
        List<ListTodo> lists = listService.getListsByUser(userId);

        log.info("✅ {} todos para la semana obtenidos", todos.size());

        model.addAttribute("userId", userId);
        model.addAttribute("todos", todos);
        model.addAttribute("lists", lists);
        model.addAttribute("selectedFilter", "Next 7 Days");
        return "todos/todos";
    }

    @GetMapping("/all")
    public String getAllTodos(Model model, HttpSession session) {
        log.info("==> GET /todos/all");

        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null) {
            log.warn("Usuario no autenticado");
            return "redirect:/login";
        }

        List<Todo> todos = todoService.getTodosByUser(userId);
        List<ListTodo> lists = listService.getListsByUser(userId);

        log.info("✅ {} todos totales obtenidos", todos.size());

        model.addAttribute("todos", todos);
        model.addAttribute("lists", lists);
        model.addAttribute("userId", userId);
        model.addAttribute("selectedFilter", "All Tasks");
        return "todos/todos";
    }

    // ✅ PARAMETRIZADAS AL FINAL - Con parámetros String
    @GetMapping("/date/{date}")
    public String getTodosByDate(@PathVariable String date, Model model, HttpSession session) {
        log.info("==> GET /todos/date/{}", date);

        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null) {
            log.warn("Usuario no autenticado");
            return "redirect:/login";
        }

        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<Todo> todos = todoService.getTodosByDate(userId, parsedDate);
            log.info("✅ {} todos para {} obtenidos", todos.size(), date);

            model.addAttribute("todos", todos);
            model.addAttribute("selectedFilter", "Tasks on " + date);

        } catch (Exception e) {
            log.error("❌ Error parsing date {}: {}", date, e.getMessage());
            model.addAttribute("error", "Formato inválido. Use: yyyy-MM-dd");
        }

        return "todos/todos";
    }

    @GetMapping("/lists")
    public String viewLists(HttpSession session, Model model) {

        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null) {
            return "redirect:/login";
        }

        List<ListTodo> lists = listService.getListsByUser(userId);
        model.addAttribute("lists", lists);

        return "todos/lists"; // Vista HTML
    }

    @GetMapping("/list/{listId}")
    public String getTodosByList(
            @PathVariable Long listId,
            Model model,
            HttpSession session) {

        log.info("==> GET /todos/list/{}", listId);
        log.debug("📍 listId recibido: {}", listId); // ← AGREGA ESTO

        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null) {
            log.warn("Usuario no autenticado");
            return "redirect:/login";
        }

        try {
            log.debug("🔍 Buscando lista con ID: {}", listId); // ← AGREGA ESTO

            ListTodo list = listService.getListById(listId);
            List<Todo> todos = listService.getTodosByList(listId);
            List<ListTodo> lists = listService.getListsByUser(userId);

            log.info("✅ {} tareas en la lista: {}", todos.size(), list.getName());

            model.addAttribute("userId", userId);
            model.addAttribute("todos", todos);
            model.addAttribute("lists", lists);
            model.addAttribute("listName", list.getName());
            model.addAttribute("listDescription", list.getDescription());

            return "todos/list";

        } catch (Exception e) {
            log.error("❌ EXCEPCIÓN en getTodosByList:", e); // ← AGREGA ESTO
            log.error("📋 Mensaje: {}", e.getMessage());
            log.error("🔍 Stack trace:", e);
            return "redirect:/todos/today";
        }
    }

}
