package com.example.TicketFlow.ticket.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TicketEventTestConsumer {

    private static final Logger log = LoggerFactory.getLogger(TicketEventTestConsumer.class);

    @KafkaListener(topics = "ticket-events", groupId = "ticketflow-test-group")
    public void listen(Object event) {
        log.info("[CONSUMER] Received event: {}", event);
    }
}