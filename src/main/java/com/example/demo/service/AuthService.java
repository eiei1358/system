package com.example.demo.service;

import java.util.Optional;

import com.example.demo.entity.Users;

/**
 * ログイン認証サービス。
 * <p>
 * メールアドレスとパスワードによる認証を行う。Controller はこのインターフェースにのみ依存し、
 * 実装（DBアクセス版／DB無しのモック版）を差し替え可能にする。
 * </p>
 *
 * @author 統合担当
 */
public interface AuthService {

    /**
     * メールアドレスとパスワードで認証する。
     *
     * @param email    メールアドレス
     * @param password パスワード
     * @return 認証に成功した社員（一致しなければ空）。状態（有効/無効）の判定は呼び出し側で行う。
     */
    Optional<Users> authenticate(String email, String password);
}
