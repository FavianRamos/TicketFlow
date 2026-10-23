package com.example.TicketFlow.user.domain;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String username;
    private String email;
    private String password;
    private UserRole role;
    private String fullName;
    private boolean active;
    private LocalDateTime createdAt;


    public static User create(String username, String email, String hashedPassword,
                              String fullName, UserRole role) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.password = hashedPassword;
        user.fullName = fullName;
        user.role = role;
        user.active = true;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    public static User reconstitute(Long id, String username, String email, String password,
                                    UserRole role, String fullName, boolean active, LocalDateTime createdAt) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.email = email;
        user.password = password;
        user.role = role;
        user.fullName = fullName;
        user.active = active;
        user.createdAt = createdAt;
        return user;
    }


    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public boolean isAgent() {
        return this.role == UserRole.AGENT;
    }

    public boolean isCustomer() {
        return this.role == UserRole.CUSTOMER;
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public UserRole getRole() { return role; }
    public String getFullName() { return fullName; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
}