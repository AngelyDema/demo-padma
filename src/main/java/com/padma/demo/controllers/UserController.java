package com.padma.demo.controllers;

import com.padma.demo.models.User;
import com.padma.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RestController
@RequestMapping("/api/users") // Prefijo general para todos los endpoints de usuarios
public class UserController {

    private final UserService userService;

    // Constructor-based injection 
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    
    // Registrar usuario nuevo

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String lastname = request.get("lastname");
            String email = request.get("email");
            String password = request.get("password");

            User users = userService.userRegister(name, lastname, email, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================
    // 2️⃣ Login 
    // ============================
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            User users = userService.login(email, password);

            return ResponseEntity.ok(Map.of(
                    "message", "Inicio de sesión exitoso",
                    "user_id", users
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ============================
    // 3️⃣ Crear usuario directo (
    // ============================
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User users) {
        User created = userService.createUser(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ============================
    // 4️⃣ Obtener perfil por email
    // ============================
    @GetMapping("/by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        try {
            User users = userService
                    .login(email, ""); // ⚠️ no se valida password, solo ejemplo (ideal: método específico en el service)
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }

    // ============================
    // 5️⃣ Endpoint de prueba / estado
    // ============================
    @GetMapping("/status")
    public String status() {
        return "✅ API UserController funcionando correctamente.";
    }
}

