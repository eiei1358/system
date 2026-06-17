package com.example.furima.controller;

import com.example.furima.dto.LoginRequest;
import com.example.furima.dto.LoginResponse;
import com.example.furima.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/password-change")
    public ResponseEntity<?> changePassword(
            @RequestParam Integer userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        try {
            userService.changePassword(userId, oldPassword, newPassword);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "パスワードが正常に変更されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/password-reset-admin")
    public ResponseEntity<?> resetPasswordByAdmin(
            @RequestParam Integer userId,
            @RequestParam Integer adminId) {
        try {
            userService.resetPasswordByAdmin(userId, adminId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "仮パスワードが送信されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> resetPasswordRequest(@RequestParam String email) {
        try {
            userService.resetPasswordByUser(email);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "仮パスワードが送信されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
