package com.example.furima.service;

import com.example.furima.dto.LoginRequest;
import com.example.furima.dto.LoginResponse;
import com.example.furima.dto.UserDTO;
import com.example.furima.entity.LoginHistory;
import com.example.furima.entity.User;
import com.example.furima.repository.LoginHistoryRepository;
import com.example.furima.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final EmailService emailService;

    @Transactional
    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        if (user.getStatus() == 2) {
            throw new RuntimeException("このアカウントはロックされています。管理者にお問い合わせください。");
        }

        String hashedPassword = hashPassword(request.getPassword());
        if (!hashedPassword.equals(user.getPassword())) {
            user.setLoginFailCount(user.getLoginFailCount() + 1);
            if (user.getLoginFailCount() >= 3) {
                user.setStatus(2);
            }
            userRepository.save(user);
            throw new RuntimeException("メールアドレスまたはパスワードが正しくありません");
        }

        LocalDateTime previousLoginAt = user.getLoginAt();
        user.setLoginAt(LocalDateTime.now());
        user.setLoginFailCount(0);
        user.setTemporaryPassword(false);

        if (user.getLastPasswordChange() == null ||
            user.getLastPasswordChange().plusDays(90).isBefore(LocalDateTime.now())) {
            user.setTemporaryPassword(true);
        }

        userRepository.save(user);
        loginHistoryRepository.save(LoginHistory.builder()
                .user(user)
                .loginAt(LocalDateTime.now())
                .build());

        String sessionToken = generateSessionToken(user.getUserId());

        return LoginResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .previousLoginAt(previousLoginAt)
                .requiresPasswordChange(user.getTemporaryPassword())
                .sessionToken(sessionToken)
                .build();
    }

    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        String oldHashedPassword = hashPassword(oldPassword);
        if (!oldHashedPassword.equals(user.getPassword())) {
            throw new RuntimeException("現在のパスワードが正しくありません");
        }

        String newHashedPassword = hashPassword(newPassword);
        user.setPassword(newHashedPassword);
        user.setLastPasswordChange(LocalDateTime.now());
        user.setTemporaryPassword(false);
        userRepository.save(user);
    }

    @Transactional
    public void resetPasswordByAdmin(Integer userId, Integer adminId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        User admin = userRepository.findByUserId(adminId)
                .orElseThrow(() -> new RuntimeException("管理者が見つかりません"));

        if (admin.getRole() != 1) {
            throw new RuntimeException("権限がありません");
        }

        String tempPassword = generateTemporaryPassword();
        String hashedPassword = hashPassword(tempPassword);

        user.setPassword(hashedPassword);
        user.setTemporaryPassword(true);
        user.setLoginFailCount(0);
        user.setStatus(1);
        userRepository.save(user);

        sendTemporaryPasswordEmail(user.getEmail(), tempPassword);
    }

    @Transactional
    public void resetPasswordByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("メールアドレスが見つかりません"));

        String tempPassword = generateTemporaryPassword();
        String hashedPassword = hashPassword(tempPassword);

        user.setPassword(hashedPassword);
        user.setTemporaryPassword(true);
        user.setLoginFailCount(0);
        user.setStatus(1);
        userRepository.save(user);

        sendTemporaryPasswordEmail(user.getEmail(), tempPassword);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Integer userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        return mapToDTO(user);
    }

    @Transactional
    public void importUsersFromCSV(String csvData) {
        String[] lines = csvData.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            String[] fields = line.split(",");
            if (fields.length < 4) continue;

            Integer userId = Integer.parseInt(fields[0].trim());
            String name = fields[1].trim();
            String department = fields[2].trim();
            String email = fields[3].trim();

            if (userRepository.findByUserId(userId).isPresent()) {
                continue;
            }

            String tempPassword = generateTemporaryPassword();
            String hashedPassword = hashPassword(tempPassword);

            User user = User.builder()
                    .userId(userId)
                    .name(name)
                    .email(email)
                    .password(hashedPassword)
                    .role(0)
                    .department(department)
                    .status(1)
                    .createdAt(LocalDateTime.now())
                    .loginAt(LocalDateTime.now())
                    .temporaryPassword(true)
                    .lastPasswordChange(LocalDateTime.now())
                    .loginFailCount(0)
                    .build();

            userRepository.save(user);
            sendTemporaryPasswordEmail(email, tempPassword);
        }
    }

    @Transactional
    public void updateUserStatus(Integer userId, Integer status) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserInfo(Integer userId, String name, String department, String iconUrl) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
        user.setName(name);
        user.setDepartment(department);
        user.setIconUrl(iconUrl);
        userRepository.save(user);
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("パスワードハッシュ化エラー", e);
        }
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    private String generateSessionToken(Integer userId) {
        byte[] tokenBytes = new byte[32];
        new SecureRandom().nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }

    @Async
    public void sendTemporaryPasswordEmail(String email, String tempPassword) {
        String subject = "仮パスワード発行のお知らせ";
        String body = "仮パスワード: " + tempPassword + "\n\n" +
                "このパスワードでログイン後、新しいパスワードを設定してください。";
        emailService.sendEmail(email, subject, body);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .iconUrl(user.getIconUrl())
                .loginAt(user.getLoginAt())
                .lastPasswordChange(user.getLastPasswordChange())
                .loginFailCount(user.getLoginFailCount())
                .build();
    }
}
