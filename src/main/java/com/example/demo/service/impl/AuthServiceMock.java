package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.service.AuthService;

/**
 * {@link AuthService} の <b>モック（仮データ）実装</b>。
 * <p>
 * <b>【一時対応】データベース（users テーブル）がまだ無いため、メモリ上の仮ユーザで
 * ログインできるようにするための実装。</b>{@code @Primary} により DB アクセス版
 * （{@link AuthServiceImpl}）より優先して使用される。
 * </p>
 *
 * <h3>ログイン用 仮データ（ユーザー名 / メールアドレス / パスワード）</h3>
 * <table border="1">
 *   <tr><th>区分</th><th>氏名</th><th>メールアドレス</th><th>パスワード</th></tr>
 *   <tr><td>社員</td><td>山田 太郎</td><td>user@example.com</td><td>user123</td></tr>
 *   <tr><td>管理者</td><td>管理者 花子</td><td>admin@example.com</td><td>admin123</td></tr>
 * </table>
 *
 * <pre>
 * // TODO: DB（users）構築後は本クラスを削除（または @Primary を外す）し、AuthServiceImpl を使用する。
 * </pre>
 *
 * @author 統合担当
 */
@Service
@Primary
public class AuthServiceMock implements AuthService {

    /** 仮ユーザ一覧（メモリ保持）。 */
    private final List<Users> dummyUsers = new ArrayList<>();

    /**
     * 仮ユーザを初期化する。
     */
    public AuthServiceMock() {
        // 社員アカウント
        Users employee = new Users();
        employee.setUser_id(1);
        employee.setName("山田 太郎");
        employee.setEmail("user@example.com");
        employee.setPassword("user123");
        employee.setRole("社員");
        employee.setDepartment("開発部");
        employee.setStatus("有効");
        employee.setCreated_at(LocalDateTime.now());
        employee.setIcon_url(null);
        dummyUsers.add(employee);

        // 管理者アカウント
        Users admin = new Users();
        admin.setUser_id(2);
        admin.setName("管理者 花子");
        admin.setEmail("admin@example.com");
        admin.setPassword("admin123");
        admin.setRole("管理者");
        admin.setDepartment("管理部");
        admin.setStatus("有効");
        admin.setCreated_at(LocalDateTime.now());
        admin.setIcon_url(null);
        dummyUsers.add(admin);
    }

    /**
     * {@inheritDoc}
     * <p>仮ユーザの中からメールアドレスとパスワードが一致するものを返す。</p>
     */
    @Override
    public Optional<Users> authenticate(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return dummyUsers.stream()
                .filter(u -> email.equals(u.getEmail()) && password.equals(u.getPassword()))
                .findFirst();
    }
}
