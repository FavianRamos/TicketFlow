package com.example.TicketFlow.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authenticated principal: a Spring Security user that also carries the database id,
 * so controllers can identify the caller from the JWT instead of trusting request params.
 */
public class AuthenticatedUser extends org.springframework.security.core.userdetails.User {

    private final Long id;

    public AuthenticatedUser(Long id, String username, String password, boolean enabled,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isCustomer() {
        return getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
    }
}