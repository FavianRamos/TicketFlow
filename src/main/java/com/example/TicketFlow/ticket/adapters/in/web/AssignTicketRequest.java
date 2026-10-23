package com.example.TicketFlow.ticket.adapters.in.web;

import jakarta.validation.constraints.NotNull;

public record AssignTicketRequest(@NotNull Long agentId) {}