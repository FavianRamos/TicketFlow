package com.example.TicketFlow.ticket.adapters.out.persistence;

import com.example.TicketFlow.ticket.domain.TicketHistoryEntry;
import com.example.TicketFlow.ticket.ports.out.TicketHistoryRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketHistoryRepositoryAdapter implements TicketHistoryRepositoryPort {

    private final TicketHistoryJpaRepository jpaRepository;

    public TicketHistoryRepositoryAdapter(TicketHistoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketHistoryEntry save(TicketHistoryEntry entry) {
        TicketHistoryJpaEntity entity = TicketHistoryJpaEntity.builder()
                .id(entry.getId())
                .ticketId(entry.getTicketId())
                .changedById(entry.getChangedById())
                .field(entry.getField())
                .oldValue(entry.getOldValue())
                .newValue(entry.getNewValue())
                .changedAt(entry.getChangedAt())
                .build();

        TicketHistoryJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<TicketHistoryEntry> findByTicketId(Long ticketId) {
        return jpaRepository.findByTicketId(ticketId).stream().map(this::toDomain).toList();
    }

    private TicketHistoryEntry toDomain(TicketHistoryJpaEntity entity) {
        return TicketHistoryEntry.reconstitute(
                entity.getId(), entity.getTicketId(), entity.getChangedById(),
                entity.getField(), entity.getOldValue(), entity.getNewValue(), entity.getChangedAt()
        );
    }
}