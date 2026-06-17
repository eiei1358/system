package com.example.furima.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@furima.local");
            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendContractNotificationEmail(String sellerEmail, String sellerName,
                                             String buyerEmail, String buyerName,
                                             String itemName, String transactionUrl) {
        String sellerSubject = "物品の成約通知";
        String sellerBody = String.format(
                "お疲れ様です。\n\n" +
                "以下の物品が成約いたしました。\n\n" +
                "物品: %s\n" +
                "購入者: %s\n\n" +
                "取引詳細: %s\n\n" +
                "よろしくお願いいたします。",
                itemName, buyerName, transactionUrl
        );

        String buyerSubject = "物品の成約通知";
        String buyerBody = String.format(
                "お疲れ様です。\n\n" +
                "以下の物品の成約が確定いたしました。\n\n" +
                "物品: %s\n" +
                "出品者: %s\n\n" +
                "取引詳細: %s\n\n" +
                "よろしくお願いいたします。",
                itemName, sellerName, transactionUrl
        );

        sendEmail(sellerEmail, sellerSubject, sellerBody);
        sendEmail(buyerEmail, buyerSubject, buyerBody);
    }

    @Async
    public void sendMessageNotificationEmail(String receiverEmail, String senderName,
                                            String itemName, String messageContent) {
        String subject = "新しいメッセージが届きました";
        String body = String.format(
                "%sさんから新しいメッセージが届きました。\n\n" +
                "物品: %s\n" +
                "メッセージ内容:\n%s",
                senderName, itemName, messageContent
        );
        sendEmail(receiverEmail, subject, body);
    }
}
