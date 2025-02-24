package com.support.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCommentRequest {
    @NotBlank(message = "Comment content is required")
    private String content;

    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
} 