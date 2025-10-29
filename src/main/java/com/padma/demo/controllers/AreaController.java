package com.padma.demo.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import com.padma.demo.models.Area;
import com.padma.demo.services.AreaService;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    // Crear Área
    @PostMapping("/")
    @Transactional
    public ResponseEntity<?> createArea(@RequestBody Area area) {
        try {
            Area createdArea = areaService.createArea(area);
            return ResponseEntity.status(201).body(createdArea);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
