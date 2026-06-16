package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Users;

/**
 * 社員（users）リポジトリ。
 * <p>
 * ログイン認証（メールアドレス＋パスワード照合）に利用する。
 * エンティティはクラス設計書（CSV）準拠の {@link Users}（複数形・snake_case）。
 * </p>
 * <p>
 * 【統合メモ】ログイン担当が作成していた {@code com.example.demo.Repository.UserRepository}
 * （{@code User} 単数・camelCase）は、契約（CSV）の {@code Users} に統一するため本リポジトリへ集約した。
 * </p>
 *
 * @author 統合担当
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {

    /**
     * メールアドレスで社員を取得する。
     *
     * @param email メールアドレス
     * @return 該当社員（存在しなければ空）
     */
    Optional<Users> findByEmail(String email);

    /**
     * メールアドレスとパスワードが一致する社員を取得する（ログイン認証用）。
     *
     * @param email    メールアドレス
     * @param password パスワード
     * @return 該当社員（存在しなければ空）
     */
    Optional<Users> findByEmailAndPassword(String email, String password);
}
