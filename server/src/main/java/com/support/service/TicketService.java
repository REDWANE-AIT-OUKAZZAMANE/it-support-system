package com.support.service;

import com.support.dto.CreateTicketRequest;
import com.support.dto.TicketDTO;
import com.support.entity.AuditLog;
import com.support.entity.Ticket;
import com.support.entity.User;
import com.support.repository.AuditLogRepository;
import com.support.repository.TicketRepository;
import com.support.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public TicketDTO createTicket(CreateTicketRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCategory(request.getCategory());
        ticket.setCreatedBy(user);
        ticket.setStatus(Ticket.Status.NEW);

        ticket = ticketRepository.save(ticket);

        AuditLog auditLog = new AuditLog();
        auditLog.setTicket(ticket);
        auditLog.setUser(user);
        auditLog.setAction("TICKET_CREATED");
        auditLog.setOldValue("NONE");
        auditLog.setNewValue(ticket.getStatus().name());
        auditLogRepository.save(auditLog);

        return convertToDTO(ticket);
    }

    @Transactional
    public TicketDTO updateTicketStatus(Long ticketId, Ticket.Status newStatus) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(newStatus);
        ticket = ticketRepository.save(ticket);

        createAuditLog(ticket, user, "STATUS_CHANGED", oldStatus, newStatus.name());

        return convertToDTO(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsForCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Ticket> tickets;
        if (user.getRole() == User.Role.IT_SUPPORT) {
            tickets = ticketRepository.findAll();
        } else {
            tickets = ticketRepository.findByCreatedBy(user);
        }

        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TicketDTO> searchTickets(Long ticketId, Ticket.Status status) {
        List<Ticket> tickets;
        if (ticketId != null) {
            tickets = ticketRepository.findById(ticketId)
                    .map(List::of)
                    .orElse(List.of());
        } else if (status != null) {
            tickets = ticketRepository.findByStatus(status);
        } else {
            tickets = ticketRepository.findAll();
        }

        return tickets.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private void createAuditLog(Ticket ticket, User user, String action, String oldValue, String newValue) {
        AuditLog auditLog = new AuditLog();
        auditLog.setTicket(ticket);
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setOldValue(oldValue != null ? oldValue : "");
        auditLog.setNewValue(newValue != null ? newValue : "");
        auditLogRepository.save(auditLog);
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTitle(ticket.getTitle());
        dto.setDescription(ticket.getDescription());
        dto.setPriority(ticket.getPriority());
        dto.setCategory(ticket.getCategory());
        dto.setStatus(ticket.getStatus());
        dto.setCreationDate(ticket.getCreationDate());
        dto.setCreatedByUsername(ticket.getCreatedBy().getUsername());
        return dto;
    }
} 