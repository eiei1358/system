package com.example.furima.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "condition", nullable = false)
    private Integer condition;

    @Column(name = "price", precision = 8, scale = 2)
    private BigDecimal price;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "status", nullable = false)
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "category_id", insertable = false, updatable = false)
    private Integer categoryId;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "place", nullable = false)
    private Integer place;

    @Column(name = "negotiable", nullable = false)
    @Builder.Default
    private Boolean negotiable = false;

    @Column(name = "reported", nullable = false)
    @Builder.Default
    private Boolean reported = false;
}
