package com.example.TicketFlow.user.application;

import com.example.TicketFlow.user.domain.*;
import com.example.TicketFlow.user.ports.in.RegisterUserCommand;
import com.example.TicketFlow.user.ports.in.UserUseCase;
import com.example.TicketFlow.user.ports.out.PasswordEncoderPort;
import com.example.TicketFlow.user.ports.out.UserRepositoryPort;

import java.util.List;

public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public UserService(UserRepositoryPort userRepository, PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterUserCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new UsernameAlreadyExistsException(command.username());
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        String hashedPassword = passwordEncoder.encode(command.rawPassword());

        User user = User.create(
                command.username(),
                command.email(),
                hashedPassword,
                command.fullName(),
                command.role()
        );

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> listByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.deactivate();
        return userRepository.save(user);
    }

    @Override
    public User activateUser(Long id) {
        User user = getUserById(id);
        user.activate();
        return userRepository.save(user);
    }
}