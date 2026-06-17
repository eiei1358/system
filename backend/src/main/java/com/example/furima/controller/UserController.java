package com.example.furima.controller;

import com.example.furima.dto.UserDTO;
import com.example.furima.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable Integer userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PostMapping("/csv-import")
    public ResponseEntity<?> importUsersFromCSV(@RequestBody Map<String, String> request) {
        try {
            String csvData = request.get("csvData");
            userService.importUsersFromCSV(csvData);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "CSVデータが正常にインポートされました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Integer userId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer status = request.get("status");
            userService.updateUserStatus(userId, status);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "ユーザーステータスが更新されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{userId}/info")
    public ResponseEntity<?> updateUserInfo(
            @PathVariable Integer userId,
            @RequestBody UserDTO userDTO) {
        try {
            userService.updateUserInfo(userId, userDTO.getName(), userDTO.getDepartment(), userDTO.getIconUrl());
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "ユーザー情報が更新されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
