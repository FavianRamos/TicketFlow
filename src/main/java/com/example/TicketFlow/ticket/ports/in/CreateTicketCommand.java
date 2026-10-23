package com.example.TicketFlow.ticket.ports.in;

import com.example.TicketFlow.ticket.domain.TicketPriority;

public record CreateTicketCommand(
        String title,
        String description,
        TicketPriority priority,
        String category,
        Long createdById
) {}