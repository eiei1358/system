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
public class MessageDTO {
    private Integer messageId;
    private Integer senderId;
    private String senderName;
    private Integer receiverId;
    private String receiverName;
    private Integer itemId;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isAdminMessage;
    private Integer status;
}
