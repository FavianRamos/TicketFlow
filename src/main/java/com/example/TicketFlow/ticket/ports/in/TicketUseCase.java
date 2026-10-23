package com.example.TicketFlow.ticket.ports.in;

import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.domain.TicketComment;
import com.example.TicketFlow.ticket.domain.TicketHistoryEntry;
import com.example.TicketFlow.ticket.domain.TicketStatus;

import java.util.List;

public interface TicketUseCase {
    Ticket createTicket(CreateTicketCommand command);
    Ticket changeStatus(Long ticketId, TicketStatus newStatus);
    Ticket assignTicket(Long ticketId, Long agentId);
    Ticket getTicket(Long id);
    List<Ticket> listTickets();
    List<Ticket> listTicketsByAgent(Long agentId);
    List<Ticket> listTicketsByCustomer(Long customerId);

    TicketComment addComment(Long ticketId, Long authorId, String message, boolean internal);
    List<TicketComment> getComments(Long ticketId);
    List<TicketHistoryEntry> getHistory(Long ticketId);
}