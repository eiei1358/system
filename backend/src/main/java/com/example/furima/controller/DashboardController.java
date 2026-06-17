package com.example.furima.controller;

import com.example.furima.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/condition-breakdown")
    public ResponseEntity<List<Map<String, Object>>> getConditionBreakdown() {
        List<Map<String, Object>> data = dashboardService.getItemsByCondition();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/type-breakdown")
    public ResponseEntity<List<Map<String, Object>>> getTypeBreakdown() {
        List<Map<String, Object>> data = dashboardService.getItemsByType();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/recent-transactions")
    public ResponseEntity<List<Map<String, Object>>> getRecentTransactions(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        List<Map<String, Object>> transactions = dashboardService.getRecentTransactions(limit);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user-activity")
    public ResponseEntity<Map<String, Object>> getUserActivity() {
        Map<String, Object> stats = dashboardService.getUserActivityStats();
        return ResponseEntity.ok(stats);
    }
}
