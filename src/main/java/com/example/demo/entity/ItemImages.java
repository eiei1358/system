package com.example.demo.entity;

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
 * 物品画像エンティティ。
 * <p>
 * 【重要 / 共有コントラクト】本クラスはチーム共有のクラス設計書（item_images.csv）に基づく定義です。
 * クラス名・プロパティ名・型は1文字も変更してはいけません。既に他メンバーが生成済みの場合は
 * そちらを正とし、本ファイルは重複生成しないでください。
 * </p>
 *
 * @author item-101 担当
 */
@Entity
@Table(name = "item_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemImages {

    /** 画像ID（主キー） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer image_id;

    /** 物品ID（items.item_id） */
    @Column(name = "item_id")
    private Integer item_id;

    /** 画像パス */
    @Column(name = "image_url")
    private String image_url;
}
