package com.example.TicketFlow.ticket.adapters.in.web;

import com.example.TicketFlow.ticket.domain.TicketHistoryEntry;

import java.time.LocalDateTime;

public record HistoryResponse(
        Long id, Long ticketId, Long changedById, String field,
        String oldValue, String newValue, LocalDateTime changedAt
) {
    public static HistoryResponse from(TicketHistoryEntry entry) {
        return new HistoryResponse(
                entry.getId(), entry.getTicketId(), entry.getChangedById(),
                entry.getField(), entry.getOldValue(), entry.getNewValue(), entry.getChangedAt()
        );
    }
}