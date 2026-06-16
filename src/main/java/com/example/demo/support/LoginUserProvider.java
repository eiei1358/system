package com.example.demo.support;

import org.springframework.stereotype.Component;

import com.example.demo.entity.Users;

import jakarta.servlet.http.HttpSession;

/**
 * ログイン中ユーザ情報の取得ヘルパー。
 * <p>
 * 認証済みユーザ（user_id, role 等）はセッションから取得する前提だが、
 * <b>ログイン機能は別メンバーが並行開発中</b>のため、現時点ではセッションに
 * ユーザが存在しない場合に動作確認用のダミーユーザを返す。
 * </p>
 *
 * <pre>
 * // TODO: 並行開発中のログイン機能（認証・セッション格納）と結合する。
 * //       結合後はダミー生成処理を削除し、未ログイン時はログイン画面へリダイレクトする想定。
 * </pre>
 *
 * @author item-101 担当
 */
@Component
public class LoginUserProvider {

    /** ログイン機能が共有で使用するセッション属性名（結合時に正式名へ要調整）。 */
    public static final String SESSION_KEY_LOGIN_USER = "loginUser";

    /**
     * 現在ログイン中のユーザを取得する。
     * <p>
     * セッションに {@link #SESSION_KEY_LOGIN_USER} が格納されていればそれを返し、
     * 無ければ動作確認用のダミーユーザ（一般社員）を返す。
     * </p>
     *
     * @param session 現在のHTTPセッション。null 不可
     * @return ログイン中ユーザ（未ログイン時はダミーユーザ。null は返さない）
     */
    public Users getCurrentUser(HttpSession session) {
        Object attr = session.getAttribute(SESSION_KEY_LOGIN_USER);
        if (attr instanceof Users) {
            return (Users) attr;
        }
        // TODO: 並行開発中のログイン機能と結合する。以下はダミー（一般社員）。
        Users dummy = new Users();
        dummy.setUser_id(1);
        dummy.setName("テスト 太郎");
        dummy.setEmail("taro@example.co.jp");
        dummy.setRole("社員");
        dummy.setDepartment("開発部");
        dummy.setStatus("有効");
        dummy.setIcon_url(null);
        return dummy;
    }
}
