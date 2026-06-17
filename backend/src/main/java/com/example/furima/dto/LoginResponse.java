package com.example.furima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Integer userId;
    private String name;
    private String email;
    private Integer role;
    private String department;
    private LocalDateTime previousLoginAt;
    private Boolean requiresPasswordChange;
    private String sessionToken;
}
