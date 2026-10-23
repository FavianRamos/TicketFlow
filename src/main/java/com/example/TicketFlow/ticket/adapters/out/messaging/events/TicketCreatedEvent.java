// TicketCreatedEvent.java
package com.example.TicketFlow.ticket.adapters.out.messaging.events;

import java.time.Instant;

public record TicketCreatedEvent(
        Long ticketId,
        String title,
        String status,
        Instant occurredAt
) {}