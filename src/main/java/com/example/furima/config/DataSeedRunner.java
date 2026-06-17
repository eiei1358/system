package com.example.furima.config;

import com.example.furima.entity.*;
import com.example.furima.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeedRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final ApplicationRepository applicationRepository;
    private final TransactionRepository transactionRepository;
    private final MessageRepository messageRepository;
    private final NgKeywordRepository ngKeywordRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Seed data already exists, skipping initialization");
            return;
        }

        log.info("Starting seed data initialization");

        seedCategories();
        seedUsers();
        seedItems();
        seedApplications();
        seedTransactions();
        seedMessages();
        seedNgKeywords();

        log.info("Seed data initialization completed");
    }

    private void seedCategories() {
        Category[] categories = {
                Category.builder().name("IT機器").build(),
                Category.builder().name("スマホ").build(),
                Category.builder().name("周辺機器").build()
        };

        for (Category category : categories) {
            categoryRepository.save(category);
        }
    }

    private void seedUsers() {
        String[] names = {
                "山田太郎", "佐藤花子", "鈴木健一", "田中美咲", "伊藤翔太",
                "渡辺由美", "佐藤拓也", "中村ひかり", "木村大輔", "高橋優子"
        };

        String[] departments = {"営業部", "開発部", "企画部"};

        for (int i = 0; i < 10; i++) {
            Integer userId = 26001 + i;
            String email = "user" + i + "@sample.com";
            String name = names[i];
            String department = departments[i % 3];

            User user = User.builder()
                    .userId(userId)
                    .name(name)
                    .email(email)
                    .password(hashPassword("pass"))
                    .role(0)
                    .department(department)
                    .status(1)
                    .createdAt(LocalDateTime.now())
                    .loginAt(LocalDateTime.now())
                    .temporaryPassword(false)
                    .lastPasswordChange(LocalDateTime.now())
                    .loginFailCount(0)
                    .build();

            userRepository.save(user);
            loginHistoryRepository.save(LoginHistory.builder()
                    .user(user)
                    .loginAt(LocalDateTime.now())
                    .build());
        }

        User admin = User.builder()
                .userId(99999)
                .name("管理者")
                .email("admin@sample.com")
                .password(hashPassword("pass"))
                .role(1)
                .department("管理部")
                .status(1)
                .createdAt(LocalDateTime.now())
                .loginAt(LocalDateTime.now())
                .temporaryPassword(false)
                .lastPasswordChange(LocalDateTime.now())
                .loginFailCount(0)
                .build();

        userRepository.save(admin);
        loginHistoryRepository.save(LoginHistory.builder()
                .user(admin)
                .loginAt(LocalDateTime.now())
                .build());
    }

    private void seedItems() {
        String[] itemNames = {
                "ノートPC", "キーボード", "iPhone 13", "ワイヤレスマウス", "USB-Cケーブル",
                "液晶ディスプレイ 27インチ", "iPad Pro 12.9", "Webカメラ", "ワイヤレスイヤホン", "ノートパソコンスタンド"
        };

        String[] descriptions = {
                "美品", "新品", "傷あり", "良好", "新品未使用"
        };

        List<User> users = userRepository.findAll();
        List<Category> categories = categoryRepository.findAll();

        for (int i = 0; i < 10; i++) {
            User user = users.get(i % users.size());
            Category category = categories.get(i % categories.size());

            Item item = Item.builder()
                    .user(user)
                    .name(itemNames[i])
                    .description(descriptions[i % descriptions.length])
                    .condition((i % 4) + 1)
                    .price(BigDecimal.valueOf(1000 * (i + 1)))
                    .type((i % 3) + 1)
                    .status(1)
                    .category(category)
                    .deadline(LocalDateTime.now().plusDays(30))
                    .createdAt(LocalDateTime.now())
                    .place((i % 2) + 1)
                    .negotiable(i % 2 == 0)
                    .reported(false)
                    .build();

            Item savedItem = itemRepository.save(item);

            ItemImage image = ItemImage.builder()
                    .item(savedItem)
                    .imageUrl("/images/item_" + savedItem.getItemId() + ".jpg")
                    .build();

            itemImageRepository.save(image);
        }
    }

    private void seedApplications() {
        List<Item> items = itemRepository.findAll();
        List<User> users = userRepository.findAll();

        for (int i = 0; i < Math.min(5, items.size()); i++) {
            Item item = items.get(i);
            User applicant = users.get((i + 1) % users.size());

            Application application = Application.builder()
                    .item(item)
                    .user(applicant)
                    .bidPrice(item.getPrice().multiply(BigDecimal.valueOf(0.9)))
                    .status(1)
                    .createdAt(LocalDateTime.now())
                    .build();

            applicationRepository.save(application);
        }
    }

    private void seedTransactions() {
        List<Item> items = itemRepository.findAll();
        List<User> users = userRepository.findAll();

        for (int i = 0; i < Math.min(3, items.size()); i++) {
            Item item = items.get(i);
            User buyer = users.get((i + 2) % users.size());

            item.setStatus(3);
            itemRepository.save(item);

            Transaction transaction = Transaction.builder()
                    .item(item)
                    .seller(item.getUser())
                    .buyer(buyer)
                    .status(3)
                    .completedAt(LocalDateTime.now())
                    .sellerCompleted(true)
                    .buyerCompleted(true)
                    .transferCode("TRANSFER" + UUID.randomUUID().toString().substring(0, 8))
                    .build();

            transactionRepository.save(transaction);
        }
    }

    private void seedMessages() {
        List<User> users = userRepository.findAll();
        List<Item> items = itemRepository.findAll();

        if (users.size() > 1 && items.size() > 0) {
            User sender = users.get(0);
            User receiver = users.get(1);
            Item item = items.get(0);

            String[] messageContents = {
                    "こちらの商品、購入を検討しています",
                    "配送方法について教えていただけますか？",
                    "なるほど、わかりました。よろしくお願いします"
            };

            for (String content : messageContents) {
                Message message = Message.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .item(item)
                        .content(content)
                        .createdAt(LocalDateTime.now())
                        .isAdminMessage(false)
                        .status(1)
                        .build();

                messageRepository.save(message);
            }
        }
    }

    private void seedNgKeywords() {
        String[] keywords = {"禁止ワード", "不適切な表現", "差別用語"};

        for (String keyword : keywords) {
            NgKeyword ngKeyword = NgKeyword.builder()
                    .keyword(keyword)
                    .type(3)
                    .status(1)
                    .createdAt(LocalDateTime.now())
                    .build();

            ngKeywordRepository.save(ngKeyword);
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
