package com.example.TicketFlow.ticket.adapters.in.web;

import com.example.TicketFlow.ticket.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTicketRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull TicketPriority priority,
        String category
) {}