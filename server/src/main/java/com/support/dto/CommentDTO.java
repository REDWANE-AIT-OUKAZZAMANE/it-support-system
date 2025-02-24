package com.support.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private Long ticketId;
    private String username;
    private LocalDateTime createdAt;
} 