package com.support.controller;

import com.support.dto.CreateTicketRequest;
import com.support.dto.TicketDTO;
import com.support.entity.Ticket;
import com.support.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Ticket management endpoints")
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    @Operation(summary = "Create a new ticket")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    @PutMapping("/{ticketId}/status")
    @Operation(summary = "Update ticket status")
    @PreAuthorize("hasRole('IT_SUPPORT')")
    public ResponseEntity<TicketDTO> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam Ticket.Status status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, status));
    }

    @GetMapping
    @Operation(summary = "Get tickets for current user")
    public ResponseEntity<List<TicketDTO>> getTickets() {
        return ResponseEntity.ok(ticketService.getTicketsForCurrentUser());
    }

    @GetMapping("/search")
    @Operation(summary = "Search tickets by ID or status")
    public ResponseEntity<List<TicketDTO>> searchTickets(
            @RequestParam(required = false) Long ticketId,
            @RequestParam(required = false) Ticket.Status status) {
        return ResponseEntity.ok(ticketService.searchTickets(ticketId, status));
    }
} 