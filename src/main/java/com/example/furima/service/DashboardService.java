package com.example.furima.service;

import com.example.furima.entity.*;
import com.example.furima.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final ItemRepository itemRepository;
    private final TransactionRepository transactionRepository;
    private final ApplicationRepository applicationRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Item> allItems = itemRepository.findAll();
        long totalItems = allItems.size();
        long soldItems = allItems.stream().filter(i -> i.getStatus() == 2 || i.getStatus() == 3).count();
        double conversionRate = totalItems > 0 ? (double) soldItems / totalItems * 100 : 0;

        stats.put("totalItems", totalItems);
        stats.put("soldItems", soldItems);
        stats.put("conversionRate", Math.round(conversionRate * 100.0) / 100.0);

        Map<String, Long> categoryStats = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCategory().getName(),
                        Collectors.counting()
                ));
        stats.put("categoryBreakdown", categoryStats);

        Map<String, Long> monthlyStats = allItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getCreatedAt().getYear() + "-" +
                               String.format("%02d", item.getCreatedAt().getMonthValue()),
                        Collectors.counting()
                ));
        stats.put("monthlyTrend", monthlyStats);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getItemsByCondition() {
        List<Item> allItems = itemRepository.findAll();

        Map<Integer, Long> conditionStats = allItems.stream()
                .collect(Collectors.groupingBy(Item::getCondition, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        String[] conditionLabels = {"新品", "美品", "良好", "傷あり", "ジャンク"};

        for (int i = 0; i < conditionLabels.length; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("condition", i + 1);
            entry.put("label", conditionLabels[i]);
            entry.put("count", conditionStats.getOrDefault(i + 1, 0L));
            result.add(entry);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getItemsByType() {
        List<Item> allItems = itemRepository.findAll();

        Map<Integer, Long> typeStats = allItems.stream()
                .collect(Collectors.groupingBy(Item::getType, Collectors.counting()));

        List<Map<String, Object>> result = new ArrayList<>();
        String[] typeLabels = {"無償", "有償", "オークション"};

        for (int i = 0; i < typeLabels.length; i++) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("type", i + 1);
            entry.put("label", typeLabels[i]);
            entry.put("count", typeStats.getOrDefault(i + 1, 0L));
            result.add(entry);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRecentTransactions(int limit) {
        return transactionRepository.findAll().stream()
                .filter(t -> t.getCompletedAt() != null)
                .sorted(Comparator.comparing(Transaction::getCompletedAt).reversed())
                .limit(limit)
                .map(t -> {
                    Map<String, Object> tx = new HashMap<>();
                    tx.put("transactionId", t.getTransactionId());
                    tx.put("itemName", t.getItem().getName());
                    tx.put("sellerName", t.getSeller().getName());
                    tx.put("buyerName", t.getBuyer().getName());
                    tx.put("completedAt", t.getCompletedAt());
                    return tx;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserActivityStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Item> allItems = itemRepository.findAll();
        long totalUsers = allItems.stream()
                .map(Item::getUserId)
                .distinct()
                .count();

        stats.put("totalActiveUsers", totalUsers);
        return stats;
    }
}
