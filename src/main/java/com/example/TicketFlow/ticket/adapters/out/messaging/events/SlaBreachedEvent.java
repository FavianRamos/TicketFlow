// SlaBreachedEvent.java
package com.example.TicketFlow.ticket.adapters.out.messaging.events;

import java.time.Instant;

public record SlaBreachedEvent(
        Long ticketId,
        String title,
        Instant occurredAt
) {}