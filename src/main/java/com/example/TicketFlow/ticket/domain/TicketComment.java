package com.example.TicketFlow.ticket.domain;

import java.time.LocalDateTime;

public class TicketComment {

    private Long id;
    private Long ticketId;
    private Long authorId;
    private String message;
    private boolean internal;
    private LocalDateTime createdAt;

    public static TicketComment create(Long ticketId, Long authorId, String message, boolean internal) {
        TicketComment comment = new TicketComment();
        comment.ticketId = ticketId;
        comment.authorId = authorId;
        comment.message = message;
        comment.internal = internal;
        comment.createdAt = LocalDateTime.now();
        return comment;
    }

    public Long getId() { return id; }
    public Long getTicketId() { return ticketId; }
    public Long getAuthorId() { return authorId; }
    public String getMessage() { return message; }
    public boolean isInternal() { return internal; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public static TicketComment reconstitute(Long id, Long ticketId, Long authorId,
                                             String message, boolean internal, LocalDateTime createdAt) {
        TicketComment comment = new TicketComment();
        comment.id = id;
        comment.ticketId = ticketId;
        comment.authorId = authorId;
        comment.message = message;
        comment.internal = internal;
        comment.createdAt = createdAt;
        return comment;
    }

    public void setId(Long id) { this.id = id; }
}