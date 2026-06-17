package com.example.furima.service;

import com.example.furima.dto.ApplicationDTO;
import com.example.furima.entity.*;
import com.example.furima.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    @Transactional
    public ApplicationDTO createApplication(Integer itemId, Integer userId, BigDecimal bidPrice) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品が見つかりません"));

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        if (item.getStatus() != 1) {
            throw new RuntimeException("この物品は応募できません");
        }

        if (item.getDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("登録期限が切れています");
        }

        if (item.getType() == 1 || item.getType() == 2) {
            item.setStatus(2);
            itemRepository.save(item);

            List<Application> existingApps = applicationRepository.findByItemId(itemId);
            if (existingApps.isEmpty()) {
                Application application = Application.builder()
                        .item(item)
                        .user(user)
                        .bidPrice(item.getPrice())
                        .status(2)
                        .createdAt(LocalDateTime.now())
                        .build();
                Application savedApp = applicationRepository.save(application);

                Transaction transaction = Transaction.builder()
                        .item(item)
                        .seller(item.getUser())
                        .buyer(user)
                        .status(1)
                        .build();
                Transaction savedTx = transactionRepository.save(transaction);

                sendContractEmails(item, user);
                return mapToDTO(savedApp);
            }
        }

        Application application = Application.builder()
                .item(item)
                .user(user)
                .bidPrice(bidPrice)
                .status(1)
                .createdAt(LocalDateTime.now())
                .build();

        Application savedApplication = applicationRepository.save(application);
        return mapToDTO(savedApplication);
    }

    @Transactional
    public void finalizeAuction(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品が見つかりません"));

        if (item.getDeadline().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("オークション期限はまだ終了していません");
        }

        List<Application> applications = applicationRepository.findByItemIdOrderByBidPriceDesc(itemId);

        if (applications.isEmpty()) {
            return;
        }

        Application winningApp = applications.get(0);
        winningApp.setStatus(2);
        applicationRepository.save(winningApp);

        for (int i = 1; i < applications.size(); i++) {
            applications.get(i).setStatus(3);
            applicationRepository.save(applications.get(i));
        }

        item.setStatus(2);
        itemRepository.save(item);

        Transaction transaction = Transaction.builder()
                .item(item)
                .seller(item.getUser())
                .buyer(winningApp.getUser())
                .status(1)
                .build();
        transactionRepository.save(transaction);

        sendContractEmails(item, winningApp.getUser());
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByItem(Integer itemId) {
        return applicationRepository.findByItemId(itemId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByUser(Integer userId) {
        return applicationRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(Integer applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("応募が見つかりません"));
        return mapToDTO(application);
    }

    private void sendContractEmails(Item item, User buyer) {
        User seller = item.getUser();
        String transactionUrl = "https://furima.local/transaction/" + item.getItemId();
        emailService.sendContractNotificationEmail(
                seller.getEmail(), seller.getName(),
                buyer.getEmail(), buyer.getName(),
                item.getName(), transactionUrl
        );
    }

    private ApplicationDTO mapToDTO(Application application) {
        return ApplicationDTO.builder()
                .applicationId(application.getApplicationId())
                .itemId(application.getItemId())
                .userId(application.getUserId())
                .userName(application.getUser().getName())
                .bidPrice(application.getBidPrice())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
