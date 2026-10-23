package com.example.TicketFlow.ticket.adapters.in.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests: start the real Spring context (security, JPA, Kafka)
 * and exercise the HTTP API with real JWTs, including role-based access rules.
 *
 * Requires the Docker containers (Postgres and Kafka) to be running:
 *   docker compose up -d
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // rolls back the data created in each test
@DirtiesContext
class TicketIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @PersistenceContext
    private EntityManager em;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ---------- Happy paths (agent) ----------

    @Test
    void createTicketEndToEnd() throws Exception {
        String agentToken = registerAgent();

        String response = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + agentToken)
                        .contentType("application/json")
                        .content(ticketBody("Integration test issue", "HIGH")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration test issue"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andReturn().getResponse().getContentAsString();

        Long ticketId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + agentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId));
    }

    @Test
    void createTicketWithoutAuthIsRejected() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType("application/json")
                        .content(ticketBody("No token", "LOW")))
                .andExpect(status().isForbidden());
    }

    @Test
    void changeStatusEndToEnd() throws Exception {
        String agentToken = registerAgent();
        long ticketId = createTicket(agentToken, "Ticket for status change", "MEDIUM");

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + agentToken)
                        .contentType("application/json")
                        .content("{ \"status\": \"IN_PROGRESS\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    // ---------- Role-based rules ----------

    @Test
    void customerCannotChangeTicketStatus() throws Exception {
        String customerToken = registerCustomer();
        long ticketId = createTicket(customerToken, "My ticket", "LOW");

        mockMvc.perform(patch("/api/tickets/" + ticketId + "/status")
                        .header("Authorization", "Bearer " + customerToken)
                        .contentType("application/json")
                        .content("{ \"status\": \"IN_PROGRESS\" }"))
                .andExpect(status().isForbidden());
    }

    @Test
    void customerSeesOnlyOwnTickets() throws Exception {
        String customerA = registerCustomer();
        String customerB = registerCustomer();
        createTicket(customerA, "Ticket of A", "LOW");
        createTicket(customerB, "Ticket of B", "LOW");

        mockMvc.perform(get("/api/tickets")
                        .header("Authorization", "Bearer " + customerA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Ticket of A"));
    }

    @Test
    void customerCannotViewAnotherCustomersTicket() throws Exception {
        String customerA = registerCustomer();
        String customerB = registerCustomer();
        long ticketOfB = createTicket(customerB, "Ticket of B", "LOW");

        mockMvc.perform(get("/api/tickets/" + ticketOfB)
                        .header("Authorization", "Bearer " + customerA))
                .andExpect(status().isForbidden());
    }

    @Test
    void internalCommentsAreHiddenFromCustomers() throws Exception {
        String customerToken = registerCustomer();
        String agentToken = registerAgent();
        long ticketId = createTicket(customerToken, "Ticket with comments", "MEDIUM");

        // Customer adds a public comment
        addComment(customerToken, ticketId, "Public comment", false, 201);
        // Agent adds an internal note
        addComment(agentToken, ticketId, "Internal note", true, 201);
        // Customer cannot write internal notes
        addComment(customerToken, ticketId, "Sneaky internal note", true, 403);

        // Customer only sees the public comment
        mockMvc.perform(get("/api/tickets/" + ticketId + "/comments")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Agent sees both
        mockMvc.perform(get("/api/tickets/" + ticketId + "/comments")
                        .header("Authorization", "Bearer " + agentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // ---------- Helpers ----------

    /** Registers a new user (always CUSTOMER) and returns the JWT from the response. */
    private String registerCustomer() throws Exception {
        String username = "user_" + UUID.randomUUID().toString().substring(0, 8);
        return registerUser(username);
    }

    /** Registers a user, promotes it to AGENT and logs in again to get a fresh token. */
    private String registerAgent() throws Exception {
        String username = "agent_" + UUID.randomUUID().toString().substring(0, 8);
        registerUser(username);

        em.createNativeQuery("UPDATE users SET role = 'AGENT' WHERE username = :u")
                .setParameter("u", username)
                .executeUpdate();
        em.clear(); // avoid Hibernate returning the stale cached user (role CUSTOMER)

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                { "username": "%s", "password": "Test1234" }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(loginResponse).get("token").asText();
    }

    private String registerUser(String username) throws Exception {
        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "Test1234",
                                  "email": "%s@example.com",
                                  "fullName": "Test User"
                                }
                                """.formatted(username, username)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("token").asText();
    }

    private long createTicket(String token, String title, String priority) throws Exception {
        String response = mockMvc.perform(post("/api/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(ticketBody(title, priority)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private void addComment(String token, long ticketId, String message, boolean internal, int expectedStatus)
            throws Exception {
        mockMvc.perform(post("/api/tickets/" + ticketId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                { "message": "%s", "internal": %s }
                                """.formatted(message, internal)))
                .andExpect(status().is(expectedStatus));
    }

    private String ticketBody(String title, String priority) {
        return """
                {
                  "title": "%s",
                  "description": "Created from the integration test",
                  "priority": "%s"
                }
                """.formatted(title, priority);
    }
}