package com.support.dto;

import com.support.entity.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String fullName;
    private User.Role role;
} 