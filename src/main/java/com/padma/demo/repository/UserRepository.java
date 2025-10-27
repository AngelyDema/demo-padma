package com.padma.demo.repository;

import com.padma.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar user por email
    Optional<User> findByEmail(String email);
    // Tener comprobación si ya está registrado parA validaciones
    boolean existsByEmail(String email);

}
