package com.padma.demo.services;

import org.springframework.stereotype.Service;
import com.padma.demo.models.Area;
import com.padma.demo.repository.AreaRepository;
import com.padma.demo.models.User;
import com.padma.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    public AreaService(AreaRepository areaRepository, UserRepository userRepository) {
        this.areaRepository = areaRepository;
        this.userRepository = userRepository;
    }

    // gET AREA BY ID
    public Area getAreaById(Long areaId) {
        return areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Área no encontrada con ID: " + areaId));
    }

    // Crear un área
    @Transactional
    public Area createArea(Area area) {

        // --- 1) resolver siempre el User ---
        User user = area.getUsers();
        if (user == null || user.getUserId() == null) {
            throw new RuntimeException("User information is missing in Area or userId is null");
        }
        Long userId = user.getUserId();
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        area.setUsers(existingUser); // ahora es managed y tiene todos los campos

        if (area.getName() == null) {
            area.setName("Area without name");
        }

        if (area.getDescription() == null) {
            area.setDescription("Area without description");
        }

        return areaRepository.save(area);
    }

    // Obtener todos los hábitos de un usuario
    public List<Area> getAreasByUser(Long userId) {
        return areaRepository.findAllByUsers_UserId(userId);
    }

    // editar area

    @Transactional
    public Area updateArea(Long areaId, Area updatedData) {

        Area existing = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found with id: " + areaId));

        if (updatedData.getName() != null && !updatedData.getName().trim().isEmpty()) {
            existing.setName(updatedData.getName());
        }

        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }

        return areaRepository.save(existing);
    }

    // DELETE AREA
    @Transactional
    public void deleteArea(Long areaId) {

        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Area not found with id: " + areaId));

        areaRepository.delete(area);
    }

}
