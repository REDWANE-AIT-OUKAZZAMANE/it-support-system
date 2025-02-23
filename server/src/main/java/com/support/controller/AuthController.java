package com.support.controller;

import com.support.dto.LoginRequest;
import com.support.dto.UserDTO;
import com.support.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password",
               description = "Authenticates a user and returns their details. Use the returned credentials for subsequent requests.")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.authenticateUser(request.getUsername(), request.getPassword()));
    }
} 