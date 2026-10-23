package com.example.TicketFlow.user.ports.out;

import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAll();
    List<User> findByRole(UserRole role);
}