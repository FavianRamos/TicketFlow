package com.example.TicketFlow.auth.adapters.in.web;

import com.example.TicketFlow.security.AuthenticatedUser;
import com.example.TicketFlow.security.JwtService;
import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;
import com.example.TicketFlow.user.ports.in.RegisterUserCommand;
import com.example.TicketFlow.user.ports.in.UserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserUseCase userUseCase;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserUseCase userUseCase) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userUseCase = userUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.username(), request.email(), request.password(),
                request.fullName(), UserRole.CUSTOMER
        );
        User user = userUseCase.registerUser(command);

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new LoginResponse(token, user.getUsername(), user.getRole().name(), user.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_CUSTOMER")
                .replace("ROLE_", "");

        String token = jwtService.generateToken(request.username(), role);
        return ResponseEntity.ok(new LoginResponse(token, principal.getUsername(), role, principal.getId()));
    }
}