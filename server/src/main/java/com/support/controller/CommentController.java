package com.support.controller;

import com.support.dto.AddCommentRequest;
import com.support.dto.CommentDTO;
import com.support.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Add a comment to a ticket")
    public ResponseEntity<CommentDTO> addComment(@Valid @RequestBody AddCommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }

    @GetMapping("/ticket/{ticketId}")
    @Operation(summary = "Get all comments for a ticket")
    public ResponseEntity<List<CommentDTO>> getCommentsForTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(commentService.getCommentsForTicket(ticketId));
    }
} 