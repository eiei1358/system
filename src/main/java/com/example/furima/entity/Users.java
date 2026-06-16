package com.example.furima.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社員（ユーザ）エンティティ。
 * <p>
 * 【重要 / 共有コントラクト】本クラスはチーム共有のクラス設計書（users.csv）に基づく定義です。
 * クラス名・プロパティ名・型は1文字も変更してはいけません。既に他メンバーが生成済みの場合は
 * そちらを正とし、本ファイルは重複生成しないでください。
 * </p>
 *
 * @author item-101 担当
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    /** ユーザID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer user_id;

    /** 氏名 */
    @Column(name = "name")
    private String name;

    /** メールアドレス */
    @Column(name = "email")
    private String email;

    /** ハッシュ化パスワード */
    @Column(name = "password")
    private String password;

    /** 権限（社員/管理者） */
    @Column(name = "role")
    private String role;

    /** 部署 */
    @Column(name = "department")
    private String department;

    /** 状態（有効/無効） */
    @Column(name = "status")
    private String status;

    /** 作成日時 */
    @Column(name = "created_at")
    private LocalDateTime created_at;

    /** アイコンURL */
    @Column(name = "icon_url")
    private String icon_url;

    /**
     * 管理者判定。
     *
     * @return role が「管理者」の場合 true、それ以外 false
     */
    public boolean isAdmin() {
        return "管理者".equals(this.role);
    }
}
