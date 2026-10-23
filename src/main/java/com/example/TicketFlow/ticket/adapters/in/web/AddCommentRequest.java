package com.example.TicketFlow.ticket.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record AddCommentRequest(
        @NotBlank String message,
        boolean internal
) {}