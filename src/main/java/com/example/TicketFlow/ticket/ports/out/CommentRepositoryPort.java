package com.example.TicketFlow.ticket.ports.out;

import com.example.TicketFlow.ticket.domain.TicketComment;

import java.util.List;

public interface CommentRepositoryPort {
    TicketComment save(TicketComment comment);
    List<TicketComment> findByTicketId(Long ticketId);
}