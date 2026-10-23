package com.example.TicketFlow.user.ports.in;

import com.example.TicketFlow.user.domain.UserRole;

public record RegisterUserCommand(
        String username,
        String email,
        String rawPassword,
        String fullName,
        UserRole role
) {}