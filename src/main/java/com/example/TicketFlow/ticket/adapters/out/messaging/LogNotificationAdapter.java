package com.example.TicketFlow.ticket.adapters.out.messaging;

import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.domain.TicketStatus;
import com.example.TicketFlow.ticket.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

public class LogNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LogNotificationAdapter.class);

    @Override
    public void ticketCreated(Ticket ticket) {
        log.info("[NOTIFICATION] Ticket #{} created: \"{}\"", ticket.getId(), ticket.getTitle());
    }

    @Override
    public void ticketStatusChanged(Ticket ticket, TicketStatus oldStatus) {
        log.info("[NOTIFICATION] Ticket #{} changed status: {} -> {}",
                ticket.getId(), oldStatus, ticket.getStatus());
    }

    @Override
    public void slaBreached(Ticket ticket) {
        log.info("[NOTIFICATION] Ticket #{} breached its SLA deadline!", ticket.getId());
    }
}