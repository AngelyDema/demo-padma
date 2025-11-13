package com.padma.demo.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import com.padma.demo.models.Habit;
import com.padma.demo.models.Area;
import com.padma.demo.services.HabitService;
import com.padma.demo.services.AreaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.padma.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import com.padma.demo.models.User;
import java.util.Map;

/**
 * Controlador de vistas Thymeleaf.
 */
@Controller
public class WebView {

    private final UserService userService;
    private final HabitService habitService;
    private final AreaService areaService;

    public WebView(UserService userService, HabitService habitService, AreaService areaService) {
        this.userService = userService;
        this.habitService = habitService;
        this.areaService = areaService;
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

        model.addAttribute("userEmail", userEmail);
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

}
