package com.example.TicketFlow.ticket.adapters.out.persistence;

import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.domain.TicketStatus;
import com.example.TicketFlow.ticket.ports.out.TicketRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TicketRepositoryAdapter implements TicketRepositoryPort {

    private final TicketJpaRepository jpaRepository;

    public TicketRepositoryAdapter(TicketJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketJpaEntity entity = toEntity(ticket);
        TicketJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Ticket> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Ticket> findByAssignedToId(Long agentId) {
        return jpaRepository.findByAssignedToId(agentId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Ticket> findByCreatedById(Long customerId) {
        return jpaRepository.findByCreatedById(customerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Ticket> findNonFinalTickets() {
        return jpaRepository.findNonFinalTickets().stream().map(this::toDomain).toList();
    }

    @Override
    public long countActiveTicketsByAgent(Long agentId) {
        return jpaRepository.countByAssignedToIdAndStatusNot(agentId, TicketStatus.CLOSED);
    }

    private TicketJpaEntity toEntity(Ticket ticket) {
        return TicketJpaEntity.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .category(ticket.getCategory())
                .createdById(ticket.getCreatedById())
                .assignedToId(ticket.getAssignedToId())
                .slaDeadline(ticket.getSlaDeadline())
                .slaBreached(ticket.isSlaBreached())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .resolvedAt(ticket.getResolvedAt())
                .closedAt(ticket.getClosedAt())
                .build();
    }

    private Ticket toDomain(TicketJpaEntity entity) {
        return Ticket.reconstitute(
                entity.getId(), entity.getTitle(), entity.getDescription(), entity.getStatus(),
                entity.getPriority(), entity.getCategory(), entity.getCreatedById(), entity.getAssignedToId(),
                entity.getSlaDeadline(), entity.isSlaBreached(), entity.getCreatedAt(), entity.getUpdatedAt(),
                entity.getResolvedAt(), entity.getClosedAt()
        );
    }
}