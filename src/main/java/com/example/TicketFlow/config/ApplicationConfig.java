package com.example.TicketFlow.config;

import com.example.TicketFlow.ticket.application.TicketService;
import com.example.TicketFlow.ticket.ports.in.TicketUseCase;
import com.example.TicketFlow.ticket.ports.out.CommentRepositoryPort;
import com.example.TicketFlow.ticket.ports.out.NotificationPort;
import com.example.TicketFlow.ticket.ports.out.TicketHistoryRepositoryPort;
import com.example.TicketFlow.ticket.ports.out.TicketRepositoryPort;
import com.example.TicketFlow.user.application.UserService;
import com.example.TicketFlow.user.ports.in.UserUseCase;
import com.example.TicketFlow.user.ports.out.PasswordEncoderPort;
import com.example.TicketFlow.user.ports.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public TicketUseCase ticketUseCase(TicketRepositoryPort ticketRepositoryPort,
                                       NotificationPort notificationPort,
                                       CommentRepositoryPort commentRepositoryPort,
                                       TicketHistoryRepositoryPort historyRepositoryPort,
                                       UserRepositoryPort userRepositoryPort) {
        return new TicketService(ticketRepositoryPort, notificationPort,
                commentRepositoryPort, historyRepositoryPort, userRepositoryPort);
    }

    @Bean
    public UserUseCase userUseCase(UserRepositoryPort userRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort) {
        return new UserService(userRepositoryPort, passwordEncoderPort);
    }
}