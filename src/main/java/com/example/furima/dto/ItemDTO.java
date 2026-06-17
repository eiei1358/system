package com.example.furima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDTO {
    private Integer itemId;
    private Integer userId;
    private String userName;
    private String name;
    private String description;
    private Integer condition;
    private BigDecimal price;
    private Integer type;
    private Integer status;
    private Integer categoryId;
    private String categoryName;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private Integer place;
    private Boolean negotiable;
    private Boolean reported;
    private List<String> imageUrls;
}
