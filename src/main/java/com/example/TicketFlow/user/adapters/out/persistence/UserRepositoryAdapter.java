package com.example.TicketFlow.user.adapters.out.persistence;

import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;
import com.example.TicketFlow.user.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .fullName(user.getFullName())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();

        UserJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return jpaRepository.findByRole(role).stream().map(this::toDomain).toList();
    }

    private User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                entity.getId(), entity.getUsername(), entity.getEmail(), entity.getPassword(),
                entity.getRole(), entity.getFullName(), entity.isActive(), entity.getCreatedAt()
        );
    }
}