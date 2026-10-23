package com.example.TicketFlow.user.adapters.in.web;

import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id, String username, String email, UserRole role,
        String fullName, boolean active, LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole(),
                user.getFullName(), user.isActive(), user.getCreatedAt()
        );
    }
}