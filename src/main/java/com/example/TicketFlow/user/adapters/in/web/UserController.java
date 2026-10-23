package com.example.TicketFlow.user.adapters.in.web;

import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;
import com.example.TicketFlow.user.ports.in.RegisterUserCommand;
import com.example.TicketFlow.user.ports.in.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.username(), request.email(), request.password(),
                request.fullName(), request.role()
        );
        User user = userUseCase.registerUser(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(userUseCase.getUserById(id)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers(@RequestParam(required = false) UserRole role) {
        List<User> users = role != null ? userUseCase.listByRole(role) : userUseCase.listUsers();
        return ResponseEntity.ok(users.stream().map(UserResponse::from).toList());
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(userUseCase.deactivateUser(id)));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(UserResponse.from(userUseCase.activateUser(id)));
    }
}