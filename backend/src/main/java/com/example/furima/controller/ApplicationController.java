package com.example.furima.controller;

import com.example.furima.dto.ApplicationDTO;
import com.example.furima.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApplicationController {
    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<?> createApplication(
            @RequestBody Map<String, Object> request) {
        try {
            Integer itemId = (Integer) request.get("itemId");
            Integer userId = (Integer) request.get("userId");
            BigDecimal bidPrice = request.get("bidPrice") != null ?
                    BigDecimal.valueOf(((Number) request.get("bidPrice")).doubleValue()) : null;

            ApplicationDTO application = applicationService.createApplication(itemId, userId, bidPrice);
            return ResponseEntity.status(HttpStatus.CREATED).body(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{itemId}/finalize-auction")
    public ResponseEntity<?> finalizeAuction(@PathVariable Integer itemId) {
        try {
            applicationService.finalizeAuction(itemId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "オークションが確定されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<?> getApplicationsByItem(@PathVariable Integer itemId) {
        try {
            List<ApplicationDTO> applications = applicationService.getApplicationsByItem(itemId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getApplicationsByUser(@PathVariable Integer userId) {
        try {
            List<ApplicationDTO> applications = applicationService.getApplicationsByUser(userId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getApplication(@PathVariable Integer applicationId) {
        try {
            ApplicationDTO application = applicationService.getApplicationById(applicationId);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
