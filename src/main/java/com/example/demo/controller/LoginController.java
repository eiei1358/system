package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.Users;
import com.example.demo.service.AuthService;
import com.example.demo.support.LoginUserProvider;

import jakarta.servlet.http.HttpSession;

/**
 * ログイン／ログアウトおよびログイン後の振り分けを担当する Controller。
 * <p>
 * 認証は {@link AuthService} に委譲する（DB 無しの間はモック仮データで認証）。<br>
 * 認証成功後はセッションにログインユーザ（{@link Users}）を格納し、権限に応じて画面遷移する。
 * </p>
 * <p>
 * 【統合メモ】ログイン担当の {@code User}（単数）実装を、クラス設計書（CSV）準拠の
 * {@link Users}（複数）へ統一。ログイン後の社員遷移先は item-101（トップ／物品一覧）に接続した。
 * セッション属性キーは item-101 側の {@link LoginUserProvider#SESSION_KEY_LOGIN_USER} と共通化している。
 * </p>
 *
 * @author 統合担当
 */
@Controller
public class LoginController {

    private final AuthService authService;

    /**
     * コンストラクタインジェクション。
     *
     * @param authService 認証サービス（DB版／モック版を差し替え可能）
     */
    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * ルートアクセスはログイン画面へリダイレクトする。
     *
     * @return ログイン画面へのリダイレクト
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * ログイン画面を表示する。
     *
     * @return ログイン画面（templates/login.html）
     */
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    /**
     * ログイン認証を行う。
     * <p>
     * 認証成功かつアカウント有効の場合、セッションにユーザを格納し、
     * 管理者は管理者画面、社員は item-101（トップ／物品一覧）へ遷移する。
     * </p>
     *
     * @param email    入力されたメールアドレス
     * @param password 入力されたパスワード
     * @param session  HTTPセッション（ログインユーザの格納先）
     * @param model    ビューへ渡すモデル（エラーメッセージ表示用）
     * @return 遷移先ビュー名／リダイレクト先
     */
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        Users user = authService.authenticate(email, password).orElse(null);

        if (user == null) {
            model.addAttribute("error", "メールアドレスまたはパスワードが違います。");
            return "login";
        }

        if (!"有効".equals(user.getStatus())) {
            model.addAttribute("error", "このアカウントは無効です。");
            return "login";
        }

        // item-101 側（LoginUserProvider）と共通のセッションキーで格納する
        session.setAttribute(LoginUserProvider.SESSION_KEY_LOGIN_USER, user);

        if ("管理者".equals(user.getRole())) {
            return "redirect:/kanrisha";
        }
        // 社員はトップ／物品一覧（item-101）へ
        return "redirect:/item-101";
    }

    /**
     * 管理者トップ画面を表示する。
     *
     * @return 管理者画面（templates/kanrisha.html）
     */
    @GetMapping("/kanrisha")
    public String kanrisha() {
        return "kanrisha";
    }

    /**
     * ログアウトする（セッション破棄）。
     *
     * @param session HTTPセッション
     * @return ログイン画面へのリダイレクト
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
