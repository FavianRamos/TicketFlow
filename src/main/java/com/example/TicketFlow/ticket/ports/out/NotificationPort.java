package com.example.TicketFlow.ticket.ports.out;

import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.domain.TicketStatus;

public interface NotificationPort {
    void ticketCreated(Ticket ticket);
    void ticketStatusChanged(Ticket ticket, TicketStatus oldStatus);
    void slaBreached(Ticket ticket);
}