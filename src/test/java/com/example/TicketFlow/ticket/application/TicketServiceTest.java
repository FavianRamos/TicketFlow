package com.example.TicketFlow.ticket.application;

import com.example.TicketFlow.ticket.domain.*;
import com.example.TicketFlow.ticket.ports.in.CreateTicketCommand;
import com.example.TicketFlow.ticket.ports.out.CommentRepositoryPort;
import com.example.TicketFlow.ticket.ports.out.NotificationPort;
import com.example.TicketFlow.ticket.ports.out.TicketHistoryRepositoryPort;
import com.example.TicketFlow.ticket.ports.out.TicketRepositoryPort;
import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;
import com.example.TicketFlow.user.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock private TicketRepositoryPort ticketRepository;
    @Mock private NotificationPort notificationPort;
    @Mock private CommentRepositoryPort commentRepository;
    @Mock private TicketHistoryRepositoryPort historyRepository;
    @Mock private UserRepositoryPort userRepository;

    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new TicketService(
                ticketRepository, notificationPort, commentRepository, historyRepository, userRepository);
    }

    // ---------- createTicket ----------

    @Test
    void createTicketSavesAndNotifies() {
        CreateTicketCommand command = new CreateTicketCommand(
                "Título", "Descripción", TicketPriority.MEDIUM, "cat", 1L);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });
        when(userRepository.findByRole(UserRole.AGENT)).thenReturn(List.of());

        Ticket result = ticketService.createTicket(command);

        assertEquals(10L, result.getId());
        assertEquals(TicketStatus.OPEN, result.getStatus());
        verify(notificationPort).ticketCreated(result);
    }

    @Test
    void createTicketAutoAssignsLeastBusyAgent() {
        CreateTicketCommand command = new CreateTicketCommand(
                "Título", "Descripción", TicketPriority.HIGH, "cat", 1L);

        User busyAgent = mock(User.class);
        when(busyAgent.getId()).thenReturn(2L);
        User freeAgent = mock(User.class);
        when(freeAgent.getId()).thenReturn(3L);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            if (t.getId() == null) t.setId(10L);
            return t;
        });
        when(userRepository.findByRole(UserRole.AGENT)).thenReturn(List.of(busyAgent, freeAgent));
        when(ticketRepository.countActiveTicketsByAgent(2L)).thenReturn(5L);
        when(ticketRepository.countActiveTicketsByAgent(3L)).thenReturn(1L);

        Ticket result = ticketService.createTicket(command);

        assertEquals(3L, result.getAssignedToId(), "Debe asignarse al agente con menos tickets activos");
        verify(historyRepository).save(any(TicketHistoryEntry.class));
    }

    @Test
    void createTicketLeavesUnassignedWhenNoAgentsExist() {
        CreateTicketCommand command = new CreateTicketCommand(
                "Título", "Descripción", TicketPriority.LOW, "cat", 1L);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            if (t.getId() == null) t.setId(10L);
            return t;
        });
        when(userRepository.findByRole(UserRole.AGENT)).thenReturn(List.of());

        Ticket result = ticketService.createTicket(command);

        assertNull(result.getAssignedToId());
        verify(historyRepository, never()).save(any());
    }

    // ---------- changeStatus ----------

    @Test
    void changeStatusUpdatesTicketAndNotifies() {
        Ticket existing = Ticket.create("Título", "Descripción", TicketPriority.MEDIUM, "cat", 1L);
        existing.setId(5L);
        when(ticketRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = ticketService.changeStatus(5L, TicketStatus.IN_PROGRESS);

        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
        verify(notificationPort).ticketStatusChanged(result, TicketStatus.OPEN);
        verify(historyRepository).save(any(TicketHistoryEntry.class));
    }

    @Test
    void changeStatusThrowsWhenTicketNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class,
                () -> ticketService.changeStatus(99L, TicketStatus.IN_PROGRESS));
    }

    @Test
    void changeStatusPropagatesInvalidTransitionException() {
        Ticket existing = Ticket.create("Título", "Descripción", TicketPriority.MEDIUM, "cat", 1L);
        existing.setId(5L);
        when(ticketRepository.findById(5L)).thenReturn(Optional.of(existing));

        // OPEN -> CLOSED no es una transición válida
        assertThrows(InvalidStatusTransitionException.class,
                () -> ticketService.changeStatus(5L, TicketStatus.CLOSED));

        verify(notificationPort, never()).ticketStatusChanged(any(), any());
    }

    // ---------- getTicket ----------

    @Test
    void getTicketThrowsWhenNotFound() {
        when(ticketRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> ticketService.getTicket(123L));
    }

    // ---------- addComment ----------

    @Test
    void addCommentValidatesTicketExists() {
        when(ticketRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class,
                () -> ticketService.addComment(7L, 1L, "mensaje", false));

        verify(commentRepository, never()).save(any());
    }
}