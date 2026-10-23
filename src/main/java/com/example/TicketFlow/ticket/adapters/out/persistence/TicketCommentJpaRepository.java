package com.example.TicketFlow.ticket.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketCommentJpaRepository extends JpaRepository<TicketCommentJpaEntity, Long> {
    List<TicketCommentJpaEntity> findByTicketId(Long ticketId);
}