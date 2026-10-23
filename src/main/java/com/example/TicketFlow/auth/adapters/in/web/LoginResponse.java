package com.example.TicketFlow.auth.adapters.in.web;

public record LoginResponse(String token, String username, String role, Long userId) {
}