package com.support.dto;

import com.support.entity.Ticket;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketDTO {
    private Long id;
    private String title;
    private String description;
    private Ticket.Priority priority;
    private Ticket.Category category;
    private Ticket.Status status;
    private LocalDateTime creationDate;
    private String createdByUsername;
} 