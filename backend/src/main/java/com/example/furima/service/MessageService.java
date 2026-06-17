package com.example.furima.service;

import com.example.furima.dto.MessageDTO;
import com.example.furima.entity.*;
import com.example.furima.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final NgKeywordRepository ngKeywordRepository;
    private final EmailService emailService;

    @Transactional
    public MessageDTO sendMessage(Integer senderId, Integer receiverId, Integer itemId, String content) {
        validateMessageContent(content);

        User sender = userRepository.findByUserId(senderId)
                .orElseThrow(() -> new RuntimeException("送信者が見つかりません"));

        User receiver = userRepository.findByUserId(receiverId)
                .orElseThrow(() -> new RuntimeException("受信者が見つかりません"));

        Item item = null;
        if (itemId != null) {
            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("物品が見つかりません"));
        }

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .item(item)
                .content(content)
                .createdAt(LocalDateTime.now())
                .isAdminMessage(false)
                .status(1)
                .build();

        Message savedMessage = messageRepository.save(message);

        String itemName = item != null ? item.getName() : "（物品指定なし）";
        emailService.sendMessageNotificationEmail(
                receiver.getEmail(),
                sender.getName(),
                itemName,
                content
        );

        return mapToDTO(savedMessage);
    }

    @Transactional
    public void reportMessage(Integer messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("メッセージが見つかりません"));

        message.setStatus(2);
        message.setIsAdminMessage(true);
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getMessagesBetween(Integer userId, Integer otherUserId, Integer itemId) {
        return messageRepository.findMessagesBetweenUsers(userId, otherUserId, itemId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getReportedMessages(Integer adminId) {
        User admin = userRepository.findByUserId(adminId)
                .orElseThrow(() -> new RuntimeException("管理者が見つかりません"));

        if (admin.getRole() != 1) {
            throw new RuntimeException("権限がありません");
        }

        return messageRepository.findReportedMessagesForAdmin(adminId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMessage(Integer messageId, Integer userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("メッセージが見つかりません"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        if (!message.getSenderId().equals(userId) && user.getRole() != 1) {
            throw new RuntimeException("権限がありません");
        }

        messageRepository.delete(message);
    }

    private void validateMessageContent(String content) {
        List<NgKeyword> ngKeywords = ngKeywordRepository.findActiveKeywordsByTypes(Arrays.asList(2, 3));

        for (NgKeyword keyword : ngKeywords) {
            if (content.contains(keyword.getKeyword())) {
                throw new RuntimeException("メッセージに禁止ワードが含まれています: " + keyword.getKeyword());
            }
        }
    }

    private MessageDTO mapToDTO(Message message) {
        return MessageDTO.builder()
                .messageId(message.getMessageId())
                .senderId(message.getSenderId())
                .senderName(message.getSender().getName())
                .receiverId(message.getReceiverId())
                .receiverName(message.getReceiver().getName())
                .itemId(message.getItemId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isAdminMessage(message.getIsAdminMessage())
                .status(message.getStatus())
                .build();
    }
}
