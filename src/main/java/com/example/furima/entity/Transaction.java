package com.example.furima.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "item_id", insertable = false, updatable = false)
    private Integer itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "seller_id", insertable = false, updatable = false)
    private Integer sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "buyer_id", insertable = false, updatable = false)
    private Integer buyerId;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "seller_completed", nullable = false)
    @Builder.Default
    private Boolean sellerCompleted = false;

    @Column(name = "buyer_completed", nullable = false)
    @Builder.Default
    private Boolean buyerCompleted = false;

    @Column(name = "transfer_code", unique = true)
    private String transferCode;
}
