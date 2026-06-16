package com.example.demo.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 社員／管理者エンティティ（クラス設計書準拠）。
 * <p>
 * 統計画面ではログインユーザ（管理者）の判定にのみ使用する。<br>
 * 実スキーマ（DDL）に合わせ <b>role / status は INTEGER</b>（role: 0=社員, 1=管理者）。
 * </p>
 * <p>
 * 【統合メモ】実プロジェクトに既存の {@code Users} エンティティがある場合はそちらを正とし、
 * 本クラスは破棄してよい（{@code AdminStatisticsController} のモック生成箇所のみ要調整）。
 * </p>
 */
@Data
public class Users {

    /** ユーザID */
    private Integer user_id;
    /** 氏名 */
    private String name;
    /** メールアドレス */
    private String email;
    /** ハッシュ化パスワード */
    private String password;
    /** 権限（0=社員, 1=管理者） */
    private Integer role;
    /** 部署 */
    private String department;
    /** 状態（0=有効 等） */
    private Integer status;
    /** 作成日時 */
    private LocalDateTime created_at;
    /** アイコンURL */
    private String icon_url;

    /**
     * 管理者判定。
     *
     * @return role が 1（管理者）の場合 true
     */
    public boolean isAdmin() {
        return this.role != null && this.role == 1;
    }
}
