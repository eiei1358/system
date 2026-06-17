package com.example.furima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDTO {
    private Integer applicationId;
    private Integer itemId;
    private Integer userId;
    private String userName;
    private BigDecimal bidPrice;
    private Integer status;
    private LocalDateTime createdAt;
}
