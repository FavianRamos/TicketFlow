package com.example.TicketFlow.ticket.adapters.out.persistence;

import com.example.TicketFlow.ticket.domain.TicketComment;
import com.example.TicketFlow.ticket.ports.out.CommentRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentRepositoryAdapter implements CommentRepositoryPort {

    private final TicketCommentJpaRepository jpaRepository;

    public CommentRepositoryAdapter(TicketCommentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketComment save(TicketComment comment) {
        TicketCommentJpaEntity entity = TicketCommentJpaEntity.builder()
                .id(comment.getId())
                .ticketId(comment.getTicketId())
                .authorId(comment.getAuthorId())
                .message(comment.getMessage())
                .internal(comment.isInternal())
                .createdAt(comment.getCreatedAt())
                .build();

        TicketCommentJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<TicketComment> findByTicketId(Long ticketId) {
        return jpaRepository.findByTicketId(ticketId).stream().map(this::toDomain).toList();
    }

    private TicketComment toDomain(TicketCommentJpaEntity entity) {
        return TicketComment.reconstitute(
                entity.getId(), entity.getTicketId(), entity.getAuthorId(),
                entity.getMessage(), entity.isInternal(), entity.getCreatedAt()
        );
    }
}