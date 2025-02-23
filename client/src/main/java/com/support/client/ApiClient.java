package com.support.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.support.dto.CreateTicketRequest;
import com.support.dto.TicketDTO;
import com.support.entity.Ticket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    private static String credentials;
    private static String currentUserRole;

    // For testing purposes
    static void setHttpClient(HttpClient client) {
        httpClient = client;
    }

    public static void setCredentials(String username, String password) {
        if (username == null || password == null) {
            credentials = null;
            currentUserRole = null;
            return;
        }
        String encodedCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        credentials = "Basic " + encodedCredentials;
        currentUserRole = null; // Reset role when credentials change
    }

    public static String getCurrentUserRole() throws IOException, InterruptedException {
        if (credentials == null) {
            throw new IllegalStateException("No credentials set. Please log in first.");
        }

        if (currentUserRole == null) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users/current"))
                    .header("Authorization", credentials)
                    .GET()
                    .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    currentUserRole = objectMapper.readTree(response.body()).get("role").asText();
                } else {
                    throw new IOException("Failed to get current user: " + response.statusCode());
                }
            } catch (Exception e) {
                credentials = null; // Clear invalid credentials
                throw new IOException("Authentication failed: " + e.getMessage());
            }
        }
        return currentUserRole;
    }

    public static boolean isITSupport() {
        if (credentials == null) {
            return false;
        }
        
        try {
            String role = getCurrentUserRole();
            return "IT_SUPPORT".equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    public static List<TicketDTO> getTickets() throws IOException, InterruptedException {
        if (credentials == null) {
            throw new IllegalStateException("No credentials set. Please log in first.");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets"))
                .header("Authorization", credentials)
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return Arrays.asList(objectMapper.readValue(response.body(), TicketDTO[].class));
            } else {
                throw new IOException("Failed to get tickets: " + response.statusCode());
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException) {
                throw (IllegalStateException) e;
            }
            throw new IOException("Failed to get tickets: " + e.getMessage());
        }
    }

    public static TicketDTO createTicket(CreateTicketRequest ticketRequest) throws IOException, InterruptedException {
        if (credentials == null) {
            throw new IllegalStateException("No credentials set. Please log in first.");
        }

        String json = objectMapper.writeValueAsString(ticketRequest);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/tickets"))
            .header("Authorization", credentials)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to create ticket: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), TicketDTO.class);
    }

    public static TicketDTO updateTicketStatus(Long ticketId, Ticket.Status status) throws IOException, InterruptedException {
        if (credentials == null) {
            throw new IllegalStateException("No credentials set. Please log in first.");
        }

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/tickets/" + ticketId + "/status?status=" + status))
            .header("Authorization", credentials)
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to update ticket status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), TicketDTO.class);
    }
} 