package com.example.furima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private Integer transactionId;
    private Integer itemId;
    private String itemName;
    private Integer sellerId;
    private String sellerName;
    private Integer buyerId;
    private String buyerName;
    private Integer status;
    private LocalDateTime completedAt;
    private Boolean sellerCompleted;
    private Boolean buyerCompleted;
    private String transferCode;
}
