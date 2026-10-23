package com.example.TicketFlow.ticket.application;

import com.example.TicketFlow.ticket.domain.*;
import com.example.TicketFlow.ticket.ports.in.CreateTicketCommand;
import com.example.TicketFlow.ticket.ports.in.TicketUseCase;
import com.example.TicketFlow.ticket.ports.out.CommentRepositoryPort;
import com.example.TicketFlow.ticket.ports.out.NotificationPort;
import com.example.TicketFlow.ticket.ports.out.TicketHistoryRepositoryPort;
import com.example.TicketFlow.ticket.ports.out.TicketRepositoryPort;
import com.example.TicketFlow.user.domain.User;
import com.example.TicketFlow.user.domain.UserRole;
import com.example.TicketFlow.user.ports.out.UserRepositoryPort;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TicketService implements TicketUseCase {

    private final TicketRepositoryPort ticketRepository;
    private final NotificationPort notificationPort;
    private final CommentRepositoryPort commentRepository;
    private final TicketHistoryRepositoryPort historyRepository;
    private final UserRepositoryPort userRepository;

    public TicketService(TicketRepositoryPort ticketRepository,
                         NotificationPort notificationPort,
                         CommentRepositoryPort commentRepository,
                         TicketHistoryRepositoryPort historyRepository,
                         UserRepositoryPort userRepository) {
        this.ticketRepository = ticketRepository;
        this.notificationPort = notificationPort;
        this.commentRepository = commentRepository;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Ticket createTicket(CreateTicketCommand command) {
        Ticket ticket = Ticket.create(
                command.title(),
                command.description(),
                command.priority(),
                command.category(),
                command.createdById()
        );

        Ticket saved = ticketRepository.save(ticket);
        notificationPort.ticketCreated(saved);

        autoAssignAgent(saved);

        return saved;
    }

    private void autoAssignAgent(Ticket ticket) {
        List<User> agents = userRepository.findByRole(UserRole.AGENT);

        Optional<User> leastBusyAgent = agents.stream()
                .min(Comparator.comparingLong(agent -> ticketRepository.countActiveTicketsByAgent(agent.getId())));

        leastBusyAgent.ifPresent(agent -> {
            ticket.assignTo(agent.getId());
            Ticket reassigned = ticketRepository.save(ticket);

            historyRepository.save(TicketHistoryEntry.of(
                    ticket.getId(), agent.getId(), "assignedTo", null, agent.getId().toString()
            ));
        });
    }

    @Override
    public Ticket changeStatus(Long ticketId, TicketStatus newStatus) {
        Ticket ticket = getTicket(ticketId);
        TicketStatus oldStatus = ticket.getStatus();

        ticket.changeStatus(newStatus);

        Ticket saved = ticketRepository.save(ticket);

        historyRepository.save(TicketHistoryEntry.of(
                ticketId, ticket.getAssignedToId(), "status", oldStatus.name(), newStatus.name()
        ));

        notificationPort.ticketStatusChanged(saved, oldStatus);
        return saved;
    }

    @Override
    public Ticket assignTicket(Long ticketId, Long agentId) {
        Ticket ticket = getTicket(ticketId);
        Long previousAgentId = ticket.getAssignedToId();

        ticket.assignTo(agentId);
        Ticket saved = ticketRepository.save(ticket);

        historyRepository.save(TicketHistoryEntry.of(
                ticketId, agentId, "assignedTo",
                previousAgentId != null ? previousAgentId.toString() : null,
                agentId.toString()
        ));

        return saved;
    }

    @Override
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    @Override
    public List<Ticket> listTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> listTicketsByAgent(Long agentId) {
        return ticketRepository.findByAssignedToId(agentId);
    }

    @Override
    public List<Ticket> listTicketsByCustomer(Long customerId) {
        return ticketRepository.findByCreatedById(customerId);
    }

    @Override
    public TicketComment addComment(Long ticketId, Long authorId, String message, boolean internal) {
        getTicket(ticketId);
        TicketComment comment = TicketComment.create(ticketId, authorId, message, internal);
        return commentRepository.save(comment);
    }

    @Override
    public List<TicketComment> getComments(Long ticketId) {
        return commentRepository.findByTicketId(ticketId);
    }

    @Override
    public List<TicketHistoryEntry> getHistory(Long ticketId) {
        return historyRepository.findByTicketId(ticketId);
    }
}