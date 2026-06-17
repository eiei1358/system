package com.example.furima.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "role", nullable = false)
    private Integer role;

    @Column(name = "department")
    private String department;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "login_at", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "temporary_password", nullable = false)
    private Boolean temporaryPassword;

    @Column(name = "last_password_change", nullable = false)
    private LocalDateTime lastPasswordChange;

    @Column(name = "login_fail_count", nullable = false)
    @Builder.Default
    private Integer loginFailCount = 0;
}
