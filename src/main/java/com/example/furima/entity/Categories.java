package com.example.furima.entity;

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
 * カテゴリエンティティ。
 * <p>
 * 【重要 / 共有コントラクト】本クラスはチーム共有のクラス設計書（categories.csv）に基づく定義です。
 * クラス名・プロパティ名・型は1文字も変更してはいけません。既に他メンバーが生成済みの場合は
 * そちらを正とし、本ファイルは重複生成しないでください。
 * </p>
 *
 * @author item-101 担当
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categories {

    /** カテゴリID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer category_id;

    /** カテゴリ名 */
    @Column(name = "name")
    private String name;
}
