package com.example.TicketFlow.ticket.adapters.in.web;

import com.example.TicketFlow.security.AuthenticatedUser;
import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.domain.TicketComment;
import com.example.TicketFlow.ticket.ports.in.CreateTicketCommand;
import com.example.TicketFlow.ticket.ports.in.TicketUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketUseCase ticketUseCase;

    public TicketController(TicketUseCase ticketUseCase) {
        this.ticketUseCase = ticketUseCase;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        // The creator always comes from the JWT, never from the request
        CreateTicketCommand command = new CreateTicketCommand(
                request.title(), request.description(), request.priority(),
                request.category(), currentUser.getId()
        );
        Ticket ticket = ticketUseCase.createTicket(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(TicketResponse.from(ticket));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        Ticket ticket = ticketUseCase.getTicket(id);
        if (!canAccess(ticket, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(TicketResponse.from(ticket));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> listTickets(
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) Long customerId,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        List<Ticket> tickets;
        if (currentUser.isCustomer()) {
            // Customers only ever see their own tickets, whatever the filters say
            tickets = ticketUseCase.listTicketsByCustomer(currentUser.getId());
        } else if (agentId != null) {
            tickets = ticketUseCase.listTicketsByAgent(agentId);
        } else if (customerId != null) {
            tickets = ticketUseCase.listTicketsByCustomer(customerId);
        } else {
            tickets = ticketUseCase.listTickets();
        }
        return ResponseEntity.ok(tickets.stream().map(TicketResponse::from).toList());
    }

    // Restricted to ADMIN and AGENT in SecurityConfig
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request
    ) {
        Ticket ticket = ticketUseCase.changeStatus(id, request.status());
        return ResponseEntity.ok(TicketResponse.from(ticket));
    }

    // Restricted to ADMIN in SecurityConfig
    @PatchMapping("/{id}/assign")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable Long id,
            @Valid @RequestBody AssignTicketRequest request
    ) {
        Ticket ticket = ticketUseCase.assignTicket(id, request.agentId());
        return ResponseEntity.ok(TicketResponse.from(ticket));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody AddCommentRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        if (!canAccessTicket(id, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Customers cannot write internal notes
        if (Boolean.TRUE.equals(request.internal()) && currentUser.isCustomer()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // The author always comes from the JWT, never from the request
        TicketComment comment = ticketUseCase.addComment(id, currentUser.getId(), request.message(), request.internal());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentResponse.from(comment));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        if (!canAccessTicket(id, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var comments = ticketUseCase.getComments(id).stream();
        if (currentUser.isCustomer()) {
            comments = comments.filter(c -> !c.isInternal()); // hide internal notes from customers
        }
        return ResponseEntity.ok(comments.map(CommentResponse::from).toList());
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<HistoryResponse>> getHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        if (!canAccessTicket(id, currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<HistoryResponse> history = ticketUseCase.getHistory(id).stream()
                .map(HistoryResponse::from).toList();
        return ResponseEntity.ok(history);
    }

    // ---------- Access rules ----------

    /** Agents and admins can access any ticket; customers only the ones they created. */
    private boolean canAccess(Ticket ticket, AuthenticatedUser user) {
        return !user.isCustomer() || Objects.equals(user.getId(), ticket.getCreatedById());
    }

    private boolean canAccessTicket(Long ticketId, AuthenticatedUser user) {
        if (!user.isCustomer()) {
            return true;
        }
        return canAccess(ticketUseCase.getTicket(ticketId), user);
    }
}