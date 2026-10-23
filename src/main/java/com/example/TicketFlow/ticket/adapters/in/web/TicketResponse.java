package com.example.TicketFlow.ticket.adapters.in.web;

import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.domain.TicketPriority;
import com.example.TicketFlow.ticket.domain.TicketStatus;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        String category,
        Long createdById,
        Long assignedToId,
        LocalDateTime slaDeadline,
        boolean slaBreached,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt,
        LocalDateTime closedAt
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(), ticket.getTitle(), ticket.getDescription(), ticket.getStatus(),
                ticket.getPriority(), ticket.getCategory(), ticket.getCreatedById(), ticket.getAssignedToId(),
                ticket.getSlaDeadline(), ticket.isSlaBreached(), ticket.getCreatedAt(),
                ticket.getUpdatedAt(), ticket.getResolvedAt(), ticket.getClosedAt()
        );
    }
}