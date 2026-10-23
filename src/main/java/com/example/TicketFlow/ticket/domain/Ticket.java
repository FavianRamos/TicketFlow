package com.example.TicketFlow.ticket.domain;

import java.time.LocalDateTime;

public class Ticket {

    private Long id;
    private String title;
    private String description;
    private TicketStatus status;
    private TicketPriority priority;
    private String category;
    private Long createdById;
    private Long assignedToId;
    private LocalDateTime slaDeadline;
    private boolean slaBreached;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;

    public static Ticket create(String title, String description, TicketPriority priority,
                                String category, Long createdById) {
        Ticket ticket = new Ticket();
        ticket.title = title;
        ticket.description = description;
        ticket.priority = priority;
        ticket.category = category;
        ticket.createdById = createdById;
        ticket.status = TicketStatus.OPEN;
        ticket.createdAt = LocalDateTime.now();
        ticket.updatedAt = ticket.createdAt;
        ticket.slaDeadline = ticket.createdAt.plus(priority.getSlaDuration());
        ticket.slaBreached = false;
        return ticket;
    }



    public void changeStatus(TicketStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(this.status, newStatus);
        }

        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();

        if (newStatus == TicketStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }
        if (newStatus == TicketStatus.CLOSED) {
            this.closedAt = LocalDateTime.now();
        }
    }

    public static Ticket reconstitute(Long id, String title, String description, TicketStatus status,
                                      TicketPriority priority, String category, Long createdById,
                                      Long assignedToId, LocalDateTime slaDeadline, boolean slaBreached,
                                      LocalDateTime createdAt, LocalDateTime updatedAt,
                                      LocalDateTime resolvedAt, LocalDateTime closedAt) {
        Ticket ticket = new Ticket();
        ticket.id = id;
        ticket.title = title;
        ticket.description = description;
        ticket.status = status;
        ticket.priority = priority;
        ticket.category = category;
        ticket.createdById = createdById;
        ticket.assignedToId = assignedToId;
        ticket.slaDeadline = slaDeadline;
        ticket.slaBreached = slaBreached;
        ticket.createdAt = createdAt;
        ticket.updatedAt = updatedAt;
        ticket.resolvedAt = resolvedAt;
        ticket.closedAt = closedAt;
        return ticket;
    }

    public void assignTo(Long agentId) {
        if (this.status.isFinal()) {
            throw new IllegalStateException("Cannot assign a closed ticket");
        }
        this.assignedToId = agentId;
        this.updatedAt = LocalDateTime.now();
    }

    public void markSlaBreached() {
        this.slaBreached = true;
    }

    public boolean isOverdue() {
        return !status.isFinal() && LocalDateTime.now().isAfter(slaDeadline);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public TicketPriority getPriority() { return priority; }
    public String getCategory() { return category; }
    public Long getCreatedById() { return createdById; }
    public Long getAssignedToId() { return assignedToId; }
    public LocalDateTime getSlaDeadline() { return slaDeadline; }
    public boolean isSlaBreached() { return slaBreached; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public LocalDateTime getClosedAt() { return closedAt; }

    public void setId(Long id) { this.id = id; }
}

