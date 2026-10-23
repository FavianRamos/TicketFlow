package com.example.TicketFlow.ticket.ports.out;

import com.example.TicketFlow.ticket.domain.TicketHistoryEntry;

import java.util.List;

public interface TicketHistoryRepositoryPort {
    TicketHistoryEntry save(TicketHistoryEntry entry);
    List<TicketHistoryEntry> findByTicketId(Long ticketId);
}