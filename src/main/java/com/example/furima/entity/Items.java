package com.example.furima.entity;

import java.time.LocalDate;
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
 * 物品エンティティ。
 * <p>
 * 【重要 / 共有コントラクト】本クラスはチーム共有のクラス設計書（items.csv）に基づく定義です。
 * クラス名・プロパティ名・型は1文字も変更してはいけません。既に他メンバーが生成済みの場合は
 * そちらを正とし、本ファイルは重複生成しないでください。
 * </p>
 *
 * @author item-101 担当
 */
@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Items {

    /** 物品ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer item_id;

    /** 出品者ID（users.user_id） */
    @Column(name = "user_id")
    private Integer user_id;

    /** 商品名 */
    @Column(name = "name")
    private String name;

    /** 説明 */
    @Column(name = "description")
    private String description;

    /** 商品の状態（新品/新品同様/中古/ジャンク 等） */
    @Column(name = "condition")
    private String condition;

    /** 価格 */
    @Column(name = "price")
    private Double price;

    /** 形式（無償/有償/オークション 等） */
    @Column(name = "type")
    private String type;

    /** 出品状態（出品中/成約済み/譲渡完了 等） */
    @Column(name = "status")
    private String status;

    /** カテゴリID（categories.category_id） */
    @Column(name = "category_id")
    private Integer category_id;

    /** 登録期限 */
    @Column(name = "deadline")
    private LocalDate deadline;

    /** 作成日時 */
    @Column(name = "created_at")
    private LocalDateTime created_at;

    /**
     * 期限切れ判定。
     *
     * @return deadline が本日より前の場合 true（期限切れ）、それ以外 false
     */
    public boolean isExpired() {
        return this.deadline != null && this.deadline.isBefore(LocalDate.now());
    }
}
