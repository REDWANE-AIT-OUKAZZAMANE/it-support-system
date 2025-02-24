package com.support.dto;

import lombok.Data;

@Data
public class AddCommentRequest {
    private String content;
    private Long ticketId;
} 