package com.example.TicketFlow.ticket.domain;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(TicketStatus from, TicketStatus to) {
        super("Cannot transition ticket from " + from + " to " + to);
    }
}