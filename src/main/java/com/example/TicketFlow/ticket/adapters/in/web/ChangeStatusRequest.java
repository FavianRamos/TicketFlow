package com.example.TicketFlow.ticket.adapters.in.web;

import com.example.TicketFlow.ticket.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(@NotNull TicketStatus status) {}