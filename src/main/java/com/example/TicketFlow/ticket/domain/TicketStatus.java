package com.example.TicketFlow.ticket.domain;

import java.util.Map;
import java.util.Set;

public enum TicketStatus {
    OPEN, IN_PROGRESS, RESOLVED, CLOSED, REOPENED;

    private static final Map<TicketStatus, Set<TicketStatus>> VALID_TRANSITIONS = Map.of(
            OPEN,        Set.of(IN_PROGRESS),
            IN_PROGRESS, Set.of(RESOLVED),
            RESOLVED,    Set.of(CLOSED, REOPENED),
            REOPENED,    Set.of(IN_PROGRESS),
            CLOSED,      Set.of()
    );

    public boolean canTransitionTo(TicketStatus target) {
        return VALID_TRANSITIONS.get(this).contains(target);
    }

    public boolean isFinal() {
        return this == CLOSED;
    }
}