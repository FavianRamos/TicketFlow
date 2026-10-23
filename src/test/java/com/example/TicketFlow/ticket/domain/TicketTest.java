package com.example.TicketFlow.ticket.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    // ---------- Creación ----------

    @Test
    void createSetsInitialStateCorrectly() {
        Ticket ticket = Ticket.create("Impresora no enciende", "Detalle del problema",
                TicketPriority.HIGH, "hardware", 1L);

        assertEquals(TicketStatus.OPEN, ticket.getStatus());
        assertFalse(ticket.isSlaBreached());
        assertNotNull(ticket.getCreatedAt());
        assertEquals(ticket.getCreatedAt(), ticket.getUpdatedAt());
        assertNull(ticket.getAssignedToId());
        assertNull(ticket.getResolvedAt());
        assertNull(ticket.getClosedAt());
    }

    @Test
    void createCalculatesSlaDeadlineFromPriority() {
        Ticket ticket = Ticket.create("Título", "Descripción",
                TicketPriority.URGENT, "red", 1L);

        Duration diff = Duration.between(ticket.getCreatedAt(), ticket.getSlaDeadline());

        assertEquals(TicketPriority.URGENT.getSlaDuration(), diff);
    }

    // ---------- Transiciones de estado válidas ----------

    @ParameterizedTest
    @CsvSource({
            "OPEN, IN_PROGRESS",
            "IN_PROGRESS, RESOLVED",
            "RESOLVED, CLOSED",
            "RESOLVED, REOPENED",
            "REOPENED, IN_PROGRESS"
    })
    void changeStatusAllowsValidTransitions(TicketStatus from, TicketStatus to) {
        Ticket ticket = ticketInStatus(from);

        ticket.changeStatus(to);

        assertEquals(to, ticket.getStatus());
    }

    // ---------- Transiciones de estado inválidas ----------

    @ParameterizedTest
    @CsvSource({
            "OPEN, RESOLVED",
            "OPEN, CLOSED",
            "IN_PROGRESS, CLOSED",
            "IN_PROGRESS, OPEN",
            "CLOSED, OPEN",
            "CLOSED, IN_PROGRESS"
    })
    void changeStatusRejectsInvalidTransitions(TicketStatus from, TicketStatus to) {
        Ticket ticket = ticketInStatus(from);

        assertThrows(InvalidStatusTransitionException.class, () -> ticket.changeStatus(to));
        assertEquals(from, ticket.getStatus(), "El estado no debe cambiar tras una transición rechazada");
    }

    @Test
    void changeStatusToResolvedSetsResolvedAt() {
        Ticket ticket = ticketInStatus(TicketStatus.IN_PROGRESS);

        ticket.changeStatus(TicketStatus.RESOLVED);

        assertNotNull(ticket.getResolvedAt());
        assertNull(ticket.getClosedAt());
    }

    @Test
    void changeStatusToClosedSetsClosedAt() {
        Ticket ticket = ticketInStatus(TicketStatus.RESOLVED);

        ticket.changeStatus(TicketStatus.CLOSED);

        assertNotNull(ticket.getClosedAt());
    }

    // ---------- Asignación ----------

    @Test
    void assignToSetsAssignedAgent() {
        Ticket ticket = ticketInStatus(TicketStatus.OPEN);

        ticket.assignTo(42L);

        assertEquals(42L, ticket.getAssignedToId());
    }

    @Test
    void assignToClosedTicketThrows() {
        Ticket ticket = ticketInStatus(TicketStatus.CLOSED);

        assertThrows(IllegalStateException.class, () -> ticket.assignTo(42L));
    }

    // ---------- SLA / vencimiento ----------

    @Test
    void isOverdueReturnsTrueWhenDeadlinePassedAndNotFinal() {
        Ticket ticket = Ticket.reconstitute(
                1L, "Título", "Descripción", TicketStatus.IN_PROGRESS, TicketPriority.HIGH,
                "cat", 1L, null,
                LocalDateTime.now().minusHours(1), // deadline ya pasado
                false,
                LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(10),
                null, null
        );

        assertTrue(ticket.isOverdue());
    }

    @Test
    void isOverdueReturnsFalseWhenDeadlineInFuture() {
        Ticket ticket = Ticket.reconstitute(
                1L, "Título", "Descripción", TicketStatus.OPEN, TicketPriority.HIGH,
                "cat", 1L, null,
                LocalDateTime.now().plusHours(5),
                false,
                LocalDateTime.now(), LocalDateTime.now(),
                null, null
        );

        assertFalse(ticket.isOverdue());
    }

    @Test
    void isOverdueReturnsFalseWhenTicketIsClosedEvenIfDeadlinePassed() {
        Ticket ticket = Ticket.reconstitute(
                1L, "Título", "Descripción", TicketStatus.CLOSED, TicketPriority.HIGH,
                "cat", 1L, null,
                LocalDateTime.now().minusHours(5),
                false,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(3), LocalDateTime.now().minusHours(1)
        );

        assertFalse(ticket.isOverdue());
    }

    @Test
    void markSlaBreachedSetsFlag() {
        Ticket ticket = ticketInStatus(TicketStatus.OPEN);

        ticket.markSlaBreached();

        assertTrue(ticket.isSlaBreached());
    }

    // ---------- Helper ----------

    private Ticket ticketInStatus(TicketStatus status) {
        Ticket ticket = Ticket.create("Título", "Descripción", TicketPriority.MEDIUM, "cat", 1L);
        // Recorre las transiciones válidas desde OPEN hasta el estado deseado para los casos simples;
        // para estados que requieren un camino específico, se reconstituye directamente.
        return switch (status) {
            case OPEN -> ticket;
            case IN_PROGRESS -> { ticket.changeStatus(TicketStatus.IN_PROGRESS); yield ticket; }
            case RESOLVED -> {
                ticket.changeStatus(TicketStatus.IN_PROGRESS);
                ticket.changeStatus(TicketStatus.RESOLVED);
                yield ticket;
            }
            case CLOSED -> {
                ticket.changeStatus(TicketStatus.IN_PROGRESS);
                ticket.changeStatus(TicketStatus.RESOLVED);
                ticket.changeStatus(TicketStatus.CLOSED);
                yield ticket;
            }
            case REOPENED -> {
                ticket.changeStatus(TicketStatus.IN_PROGRESS);
                ticket.changeStatus(TicketStatus.RESOLVED);
                ticket.changeStatus(TicketStatus.REOPENED);
                yield ticket;
            }
        };
    }
}