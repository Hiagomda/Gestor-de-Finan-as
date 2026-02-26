package com.financeapp.repository;

import com.financeapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Acesso a dados de usuarios
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
