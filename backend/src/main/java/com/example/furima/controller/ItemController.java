package com.example.furima.controller;

import com.example.furima.dto.ItemDTO;
import com.example.furima.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<?> createItem(@RequestBody ItemDTO itemDTO) {
        try {
            ItemDTO createdItem = itemService.createItem(itemDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Integer itemId) {
        try {
            ItemDTO item = itemService.getItemById(itemId);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateItem(
            @PathVariable Integer itemId,
            @RequestBody ItemDTO itemDTO) {
        try {
            ItemDTO updatedItem = itemService.updateItem(itemId, itemDTO);
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{itemId}/extend-deadline")
    public ResponseEntity<?> extendDeadline(
            @PathVariable Integer itemId,
            @RequestBody Map<String, String> request) {
        try {
            LocalDateTime newDeadline = LocalDateTime.parse(request.get("newDeadline"));
            itemService.extendDeadline(itemId, newDeadline);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "登録期限が延長されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchItems(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer condition,
            @RequestParam(required = false, defaultValue = "1,2") String statusList,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(required = false, defaultValue = "false") Boolean includeExpired) {
        try {
            List<Integer> statuses = Arrays.stream(statusList.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .toList();

            List<ItemDTO> items = itemService.searchItems(categoryId, keyword, condition, statuses, sortBy, includeExpired);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/{itemId}/report")
    public ResponseEntity<?> reportItem(@PathVariable Integer itemId) {
        try {
            itemService.reportItem(itemId);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("message", "物品が報告されました");
            }});
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
