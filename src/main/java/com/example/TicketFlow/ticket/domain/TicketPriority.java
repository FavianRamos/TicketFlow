package com.example.TicketFlow.ticket.domain;

import java.time.Duration;

public enum TicketPriority {
    LOW(Duration.ofHours(72)),
    MEDIUM(Duration.ofHours(24)),
    HIGH(Duration.ofHours(8)),
    URGENT(Duration.ofHours(4));

    private final Duration slaDuration;

    TicketPriority(Duration slaDuration) {
        this.slaDuration = slaDuration;
    }

    public Duration getSlaDuration() {
        return slaDuration;
    }
}