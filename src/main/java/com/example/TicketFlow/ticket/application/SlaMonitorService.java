package com.example.TicketFlow.ticket.application;

import com.example.TicketFlow.ticket.domain.Ticket;
import com.example.TicketFlow.ticket.ports.out.NotificationPort;
import com.example.TicketFlow.ticket.ports.out.TicketRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlaMonitorService {

    private static final Logger log = LoggerFactory.getLogger(SlaMonitorService.class);

    private final TicketRepositoryPort ticketRepository;
    private final NotificationPort notificationPort;

    public SlaMonitorService(TicketRepositoryPort ticketRepository, NotificationPort notificationPort) {
        this.ticketRepository = ticketRepository;
        this.notificationPort = notificationPort;
    }

    @Scheduled(fixedRate = 60000) // cada 60 segundos
    public void checkOverdueTickets() {
        List<Ticket> nonFinalTickets = ticketRepository.findNonFinalTickets();

        for (Ticket ticket : nonFinalTickets) {
            if (ticket.isOverdue() && !ticket.isSlaBreached()) {
                ticket.markSlaBreached();
                ticketRepository.save(ticket);
                notificationPort.slaBreached(ticket);
                log.info("[SLA MONITOR] Ticket #{} marked as SLA breached", ticket.getId());
            }
        }
    }
}