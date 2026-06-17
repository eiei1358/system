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
public class UserDTO {
    private Integer userId;
    private String name;
    private String email;
    private Integer role;
    private String department;
    private Integer status;
    private LocalDateTime createdAt;
    private String iconUrl;
    private LocalDateTime loginAt;
    private LocalDateTime lastPasswordChange;
    private Integer loginFailCount;
}
