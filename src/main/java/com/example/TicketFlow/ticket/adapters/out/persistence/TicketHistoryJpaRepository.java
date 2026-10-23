package com.example.TicketFlow.ticket.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketHistoryJpaRepository extends JpaRepository<TicketHistoryJpaEntity, Long> {
    List<TicketHistoryJpaEntity> findByTicketId(Long ticketId);
}