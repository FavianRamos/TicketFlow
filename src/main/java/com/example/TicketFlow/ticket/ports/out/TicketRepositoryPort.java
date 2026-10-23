package com.example.TicketFlow.ticket.ports.out;

import com.example.TicketFlow.ticket.domain.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepositoryPort {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(Long id);
    List<Ticket> findAll();
    List<Ticket> findByAssignedToId(Long agentId);
    List<Ticket> findByCreatedById(Long customerId);
    List<Ticket> findNonFinalTickets(); // para la tarea programada que revisa SLA vencido
    long countActiveTicketsByAgent(Long agentId); // para la auto-asignación por menor carga
}