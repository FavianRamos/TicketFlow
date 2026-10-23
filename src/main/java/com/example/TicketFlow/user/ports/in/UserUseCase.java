package com.example.TicketFlow.user.ports.in;

import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;

import java.util.List;

public interface UserUseCase {
    User registerUser(RegisterUserCommand command);
    User getUserById(Long id);
    User getUserByUsername(String username);
    List<User> listUsers();
    List<User> listByRole(UserRole role);
    User deactivateUser(Long id);
    User activateUser(Long id);
}