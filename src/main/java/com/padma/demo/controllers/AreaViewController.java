
package com.padma.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpSession;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import com.padma.demo.models.Area;
import com.padma.demo.models.Habit;
import com.padma.demo.services.AreaService;
import com.padma.demo.services.HabitService;
import com.padma.demo.services.UserService;

@Slf4j
@Controller
@RequestMapping("/habits") // ✅ Base path específico
public class AreaViewController {

    private final UserService userService;
    private final AreaService areaService;
    private final HabitService habitService;

    public AreaViewController(UserService userService, AreaService areaService, HabitService habitService) {
        this.userService = userService;
        this.areaService = areaService;
        this.habitService = habitService;
    }

    // ✅ AREA VIEW - Show habits filtered by area
    @GetMapping("/area/{areaId}")
    public String getHabitsByArea(
            @PathVariable Long areaId,
            Model model,
            HttpSession session) {

        log.info("==> GET /habit/area/{}", areaId);
        log.debug("📍 areaId recibido: {}", areaId);

        Long userId = (Long) session.getAttribute("LOGGED_USER_ID");
        if (userId == null) {
            log.warn("Usuario no autenticado");
            return "redirect:/login";
        }

        try {
            log.debug("🔍 Buscando área con ID: {}", areaId);

            // Obtener el área
            Area area = areaService.getAreaById(areaId);

            // Verificar que el área pertenece al usuario
            if (!area.getUsers().getUserId().equals(userId)) {
                log.warn("❌ Usuario {} no tiene permiso para ver el área {}", userId, areaId);
                return "redirect:/habit";
            }

            // Obtener hábitos filtrados por área
            List<Habit> habits = habitService.getHabitsByAreaAndUser(areaId, userId);
            List<Area> areas = areaService.getAreasByUser(userId);

            log.info("✅ {} hábitos en el área: {}", habits.size(), area.getName());

            model.addAttribute("userId", userId);
            model.addAttribute("habits", habits);
            model.addAttribute("areas", areas);
            model.addAttribute("areaId", areaId);
            model.addAttribute("areaName", area.getName());
            model.addAttribute("areaDescription", area.getDescription());
            model.addAttribute("user", userService.findById(userId));

            return "habit/area"; // ← Nueva vista para mostrar hábitos de un área

        } catch (Exception e) {
            log.error("❌ EXCEPCIÓN en getHabitsByArea:", e);
            log.error("📋 Mensaje: {}", e.getMessage());
            log.error("🔍 Stack trace:", e);
            return "redirect:/habits";
        }
    }

}
