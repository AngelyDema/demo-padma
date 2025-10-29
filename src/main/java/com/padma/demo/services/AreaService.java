package com.padma.demo.services;

import org.springframework.stereotype.Service;
import com.padma.demo.models.Area;
import com.padma.demo.repository.AreaRepository;
import com.padma.demo.models.User;
import com.padma.demo.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class AreaService {

    private final AreaRepository areaRepository;
    private final UserRepository userRepository;

    public AreaService(AreaRepository areaRepository, UserRepository userRepository) {
        this.areaRepository = areaRepository;
        this.userRepository = userRepository;
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

}
