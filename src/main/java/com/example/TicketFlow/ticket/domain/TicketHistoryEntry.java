package com.example.TicketFlow.ticket.domain;

import java.time.LocalDateTime;

public class TicketHistoryEntry {

    private Long id;
    private Long ticketId;
    private Long changedById;
    private String field;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;

    public static TicketHistoryEntry of(Long ticketId, Long changedById,
                                        String field, String oldValue, String newValue) {
        TicketHistoryEntry entry = new TicketHistoryEntry();
        entry.ticketId = ticketId;
        entry.changedById = changedById;
        entry.field = field;
        entry.oldValue = oldValue;
        entry.newValue = newValue;
        entry.changedAt = LocalDateTime.now();
        return entry;
    }


    public Long getId() { return id; }
    public Long getTicketId() { return ticketId; }
    public Long getChangedById() { return changedById; }
    public String getField() { return field; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public LocalDateTime getChangedAt() { return changedAt; }

    public void setId(Long id) { this.id = id; }

    public static TicketHistoryEntry reconstitute(Long id, Long ticketId, Long changedById,
                                                  String field, String oldValue, String newValue,
                                                  LocalDateTime changedAt) {
        TicketHistoryEntry entry = new TicketHistoryEntry();
        entry.id = id;
        entry.ticketId = ticketId;
        entry.changedById = changedById;
        entry.field = field;
        entry.oldValue = oldValue;
        entry.newValue = newValue;
        entry.changedAt = changedAt;
        return entry;
    }

}