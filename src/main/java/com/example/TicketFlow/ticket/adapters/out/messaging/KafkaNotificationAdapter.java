package com.example.TicketFlow.ticket.adapters.out.messaging;

import com.example.TicketFlow.ticket.adapters.out.messaging.events.SlaBreachedEvent;
import com.example.TicketFlow.ticket.adapters.out.messaging.events.TicketCreatedEvent;
import com.example.TicketFlow.ticket.adapters.out.messaging.events.TicketStatusChangedEvent;
import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.domain.TicketStatus;
import com.example.TicketFlow.ticket.ports.out.NotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class KafkaNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationAdapter.class);
    private static final String TOPIC = "ticket-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaNotificationAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void ticketCreated(Ticket ticket) {
        var event = new TicketCreatedEvent(
                ticket.getId(), ticket.getTitle(), ticket.getStatus().name(), Instant.now());
        kafkaTemplate.send(TOPIC, ticket.getId().toString(), event);
        log.info("[KAFKA] Published TicketCreatedEvent for ticket #{}", ticket.getId());
    }

    @Override
    public void ticketStatusChanged(Ticket ticket, TicketStatus oldStatus) {
        var event = new TicketStatusChangedEvent(
                ticket.getId(), oldStatus.name(), ticket.getStatus().name(), Instant.now());
        kafkaTemplate.send(TOPIC, ticket.getId().toString(), event);
        log.info("[KAFKA] Published TicketStatusChangedEvent for ticket #{}", ticket.getId());
    }

    @Override
    public void slaBreached(Ticket ticket) {
        var event = new SlaBreachedEvent(ticket.getId(), ticket.getTitle(), Instant.now());
        kafkaTemplate.send(TOPIC, ticket.getId().toString(), event);
        log.info("[KAFKA] Published SlaBreachedEvent for ticket #{}", ticket.getId());
    }
}