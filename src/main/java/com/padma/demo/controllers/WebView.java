package com.padma.demo.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import com.padma.demo.models.Habit;
import com.padma.demo.models.Area;
import com.padma.demo.models.Todo;
import com.padma.demo.models.ListTodo;
import com.padma.demo.services.HabitService;
import com.padma.demo.services.AreaService;
import com.padma.demo.services.TodoService;
import com.padma.demo.services.ListService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.padma.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.padma.demo.models.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;

/**
 * Controlador de vistas Thymeleaf.
 */
@Controller

public class WebView {

    private final UserService userService;
    private final HabitService habitService;
    private final AreaService areaService;
    private final TodoService todoService;
    private final ListService listService;

    public WebView(UserService userService, HabitService habitService, AreaService areaService, TodoService todoService,
            ListService listService) {
        this.userService = userService;
        this.habitService = habitService;
        this.areaService = areaService;
        this.todoService = todoService;
        this.listService = listService;
    }

    @GetMapping("/")
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            User user = userService.login(email, password);
            session.setAttribute("LOGGED_USER_ID", user.getUserId());
            session.setAttribute("LOGGED_USER_EMAIL", user.getEmail());
            return "redirect:/home";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "user/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String name,
            @RequestParam String lastname,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {
        try {
            userService.userRegister(name, lastname, email, password);
            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Inicia sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("RegisterError", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/home")
    public String showUserHomePage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        String userEmail = (String) session.getAttribute("LOGGED_USER_EMAIL");

        if (userId == null) {
            return "redirect:/login";
        }

        // Obtener el usuario
        User user = userService.findById(userId);

        // Obtener tareas de hoy
        List<Todo> todos = todoService.getTodosForToday(userId);

        // Obtener hábitos del usuario
        List<Habit> habits = habitService.getHabitsByUserId(userId);

        // Agregar datos al modelo
        model.addAttribute("user", user);
        model.addAttribute("userEmail", userEmail);
        model.addAttribute("todos", todos);
        model.addAttribute("habits", habits);

        return "user/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/habits")
    public String showHabitsPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");

        // Obtener el objeto usuario desde tu servicio (opcional)
        User user = userService.findById(userId);

        if (userId == null) {
            return "redirect:/login";
        }

        List<Habit> habits = habitService.getHabitsByUserId(userId);
        List<Area> areas = areaService.getAreasByUser(userId);

        model.addAttribute("user", user);
        model.addAttribute("habits", habits);
        model.addAttribute("areas", areas);
        return "habit/habits";
    }

    @GetMapping("/todos")
    public String showTodosPage(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null)
            return "redirect:/login";

        User user = userService.findById(userId);

        List<Todo> todos = todoService.getTodosForToday(userId);
        List<ListTodo> lists = listService.getListsByUser(userId);

        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("todos", todos);
        model.addAttribute("lists", lists);
        return "todos/todos";
    }

}
