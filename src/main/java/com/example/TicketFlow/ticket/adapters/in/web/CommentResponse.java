package com.example.TicketFlow.ticket.adapters.in.web;

import com.example.TicketFlow.ticket.domain.TicketComment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id, Long ticketId, Long authorId, String message, boolean internal, LocalDateTime createdAt
) {
    public static CommentResponse from(TicketComment comment) {
        return new CommentResponse(
                comment.getId(), comment.getTicketId(), comment.getAuthorId(),
                comment.getMessage(), comment.isInternal(), comment.getCreatedAt()
        );
    }
}