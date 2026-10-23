package com.example.TicketFlow.ticket.domain;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(Long id) {
        super("Ticket not found with id: " + id);
    }
}