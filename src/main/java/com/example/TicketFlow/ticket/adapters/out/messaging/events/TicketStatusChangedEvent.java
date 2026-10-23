// TicketStatusChangedEvent.java
package com.example.TicketFlow.ticket.adapters.out.messaging.events;

import java.time.Instant;

public record TicketStatusChangedEvent(
        Long ticketId,
        String oldStatus,
        String newStatus,
        Instant occurredAt
) {}