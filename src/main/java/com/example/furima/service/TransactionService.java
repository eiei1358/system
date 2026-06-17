package com.example.furima.service;

import com.example.furima.dto.TransactionDTO;
import com.example.furima.entity.*;
import com.example.furima.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public void markSellerComplete(Integer transactionId, Integer userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("取引が見つかりません"));

        if (!transaction.getSellerId().equals(userId)) {
            throw new RuntimeException("権限がありません");
        }

        transaction.setSellerCompleted(true);
        checkAndFinalizeTransaction(transaction);
        transactionRepository.save(transaction);
    }

    @Transactional
    public void markBuyerComplete(Integer transactionId, Integer userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("取引が見つかりません"));

        if (!transaction.getBuyerId().equals(userId)) {
            throw new RuntimeException("権限がありません");
        }

        transaction.setBuyerCompleted(true);
        checkAndFinalizeTransaction(transaction);
        transactionRepository.save(transaction);
    }

    private void checkAndFinalizeTransaction(Transaction transaction) {
        if (transaction.getSellerCompleted() && transaction.getBuyerCompleted()) {
            transaction.setStatus(3);
            transaction.setCompletedAt(LocalDateTime.now());

            Item item = transaction.getItem();
            item.setStatus(3);
            itemRepository.save(item);
        }
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("取引が見つかりません"));
        return mapToDTO(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionByTransferCode(String transferCode) {
        Transaction transaction = transactionRepository.findByTransferCode(transferCode)
                .orElseThrow(() -> new RuntimeException("取引コードが見つかりません"));
        return mapToDTO(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsBySeller(Integer sellerId) {
        return transactionRepository.findBySellerId(sellerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByBuyer(Integer buyerId) {
        return transactionRepository.findByBuyerId(buyerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public String generateTransferCode(Integer transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("取引が見つかりません"));

        String transferCode = generateRandomCode();
        transaction.setTransferCode(transferCode);
        transactionRepository.save(transaction);

        return transferCode;
    }

    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 16; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .transactionId(transaction.getTransactionId())
                .itemId(transaction.getItemId())
                .itemName(transaction.getItem().getName())
                .sellerId(transaction.getSellerId())
                .sellerName(transaction.getSeller().getName())
                .buyerId(transaction.getBuyerId())
                .buyerName(transaction.getBuyer().getName())
                .status(transaction.getStatus())
                .completedAt(transaction.getCompletedAt())
                .sellerCompleted(transaction.getSellerCompleted())
                .buyerCompleted(transaction.getBuyerCompleted())
                .transferCode(transaction.getTransferCode())
                .build();
    }
}
