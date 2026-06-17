package com.example.furima.controller;

import com.example.furima.dto.TransactionDTO;
import com.example.furima.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/{transactionId}/seller-complete")
    public ResponseEntity<?> markSellerComplete(
            @PathVariable Integer transactionId,
            @RequestParam Integer userId) {
        try {
            transactionService.markSellerComplete(transactionId, userId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "出品者が譲渡完了を確認しました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{transactionId}/buyer-complete")
    public ResponseEntity<?> markBuyerComplete(
            @PathVariable Integer transactionId,
            @RequestParam Integer userId) {
        try {
            transactionService.markBuyerComplete(transactionId, userId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "購入者が受取完了を確認しました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable Integer transactionId) {
        try {
            TransactionDTO transaction = transactionService.getTransactionById(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/code/{transferCode}")
    public ResponseEntity<?> getTransactionByTransferCode(@PathVariable String transferCode) {
        try {
            TransactionDTO transaction = transactionService.getTransactionByTransferCode(transferCode);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getTransactionsBySeller(@PathVariable Integer sellerId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsBySeller(sellerId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<?> getTransactionsByBuyer(@PathVariable Integer buyerId) {
        try {
            List<TransactionDTO> transactions = transactionService.getTransactionsByBuyer(buyerId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{transactionId}/generate-transfer-code")
    public ResponseEntity<?> generateTransferCode(@PathVariable Integer transactionId) {
        try {
            String transferCode = transactionService.generateTransferCode(transactionId);
            Map<String, String> response = new HashMap<>();
            response.put("transferCode", transferCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
