package com.example.TicketFlow.ticket.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketJpaRepository extends JpaRepository<TicketJpaEntity, Long> {

    List<TicketJpaEntity> findByAssignedToId(Long agentId);

    List<TicketJpaEntity> findByCreatedById(Long customerId);

    @Query("SELECT t FROM TicketJpaEntity t WHERE t.status <> 'CLOSED'")
    List<TicketJpaEntity> findNonFinalTickets();

    long countByAssignedToIdAndStatusNot(Long agentId, com.example.TicketFlow.ticket.domain.TicketStatus status);
}