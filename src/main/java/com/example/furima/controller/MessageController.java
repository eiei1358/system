package com.example.furima.controller;

import com.example.furima.dto.MessageDTO;
import com.example.furima.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> request) {
        try {
            Integer senderId = (Integer) request.get("senderId");
            Integer receiverId = (Integer) request.get("receiverId");
            Integer itemId = (Integer) request.get("itemId");
            String content = (String) request.get("content");

            MessageDTO message = messageService.sendMessage(senderId, receiverId, itemId, content);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{messageId}/report")
    public ResponseEntity<?> reportMessage(@PathVariable Integer messageId) {
        try {
            messageService.reportMessage(messageId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "メッセージが報告されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/thread")
    public ResponseEntity<?> getMessagesBetween(
            @RequestParam Integer userId,
            @RequestParam Integer otherUserId,
            @RequestParam Integer itemId) {
        try {
            List<MessageDTO> messages = messageService.getMessagesBetween(userId, otherUserId, itemId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/reported/{adminId}")
    public ResponseEntity<?> getReportedMessages(@PathVariable Integer adminId) {
        try {
            List<MessageDTO> messages = messageService.getReportedMessages(adminId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Integer messageId,
            @RequestParam Integer userId) {
        try {
            messageService.deleteMessage(messageId, userId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "メッセージが削除されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
