package com.padma.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.padma.demo.models.User;
import com.padma.demo.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User users) {
        return userRepository.save(users);
    }

    public User userRegister(String name, String lastname, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El correo ya está en uso");
        }

        User users = new User();
        users.setName(name);
        users.setLastname(lastname);
        users.setEmail(email);
        users.setPassword(password);
        return userRepository.save(users);
    }

    public User login(String email, String password) {
        User users = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!password.equals(users.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        return users;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

}
