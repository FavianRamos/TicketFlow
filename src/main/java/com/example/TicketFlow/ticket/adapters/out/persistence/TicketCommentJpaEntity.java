package com.example.TicketFlow.ticket.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCommentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", insertable = false, updatable = false)
    private TicketJpaEntity ticket;

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(nullable = false)
    private boolean internal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}