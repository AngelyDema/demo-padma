package com.padma.demo.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.padma.demo.models.Area;
import com.padma.demo.services.AreaService;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @PostMapping("/createArea")
    @Transactional
    public ResponseEntity<?> createArea(@RequestBody Area area) {
        log.info("==> POST /api/areas/createArea");
        log.debug("📋 Payload recibido: {}", area);

        try {
            // Validar nombre
            if (area.getName() == null || area.getName().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El nombre del área es requerido"));
            }

            // Validar usuario
            if (area.getUsers() == null || area.getUsers().getUserId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Usuario no especificado"));
            }

            Area createdArea = areaService.createArea(area);
            log.info("✅ Área creada: {}", createdArea.getName());

            // Devolver JSON simple (sin relaciones circulares)
            Map<String, Object> response = new HashMap<>();
            response.put("areaId", createdArea.getAreaId());
            response.put("name", createdArea.getName());
            response.put("description", createdArea.getDescription());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("❌ Error creando área: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // UPDATE AREA

    @PutMapping("/id/{areaId}")
    public ResponseEntity<?> updateArea(
            @PathVariable Long areaId,
            @RequestBody Area updatedArea) {

        log.info("==> PUT /api/areas/id/{}", areaId);
        log.debug("📋 Payload recibido: {}", updatedArea);

        try {
            Area updated = areaService.updateArea(areaId, updatedArea);

            Map<String, Object> res = new HashMap<>();
            res.put("areaId", updated.getAreaId());
            res.put("name", updated.getName());
            res.put("description", updated.getDescription());

            return new ResponseEntity<>(res, HttpStatus.OK);

        } catch (RuntimeException e) {
            log.error("❌ Error actualizando área: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE AREA

    @DeleteMapping("/id/{areaId}")
    public ResponseEntity<?> deleteArea(@PathVariable Long areaId) {

        log.info("==> DELETE /api/areas/id/{}", areaId);

        try {
            areaService.deleteArea(areaId);
            return ResponseEntity.ok(Map.of("message", "Área eliminada exitosamente"));
        } catch (RuntimeException e) {
            log.error("❌ Error eliminando área: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
