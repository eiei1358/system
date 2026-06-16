package com.example.furima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * item-101（物品一覧画面）の一覧 1 行（カード 1 枚）に表示するための画面表示用 DTO。
 * <p>
 * エンティティ {@code Items} をそのまま画面に渡すのではなく、
 * 「価格表示ラベル」「残り日数」「サムネイルURL」「カテゴリ名」など
 * 画面表示に必要な加工済みの値をまとめて保持する。<br>
 * 本 DTO は item-101 画面専用であり、永続化やAPI契約には使用しない。
 * </p>
 *
 * @author item-101 担当
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSummaryDto {

    /** 物品ID（詳細画面 item-107 への遷移キー） */
    private Integer item_id;

    /** 商品名 */
    private String name;

    /** カテゴリ名（カテゴリ未設定時は空文字） */
    private String categoryName;

    /** 商品の状態（新品/中古 等） */
    private String condition;

    /**
     * 価格表示ラベル。
     * 形式（type）に応じて「無償」「¥1,500」「オークション」などの表示文字列を保持する。
     */
    private String priceLabel;

    /** 出品状態（出品中/成約済み 等） */
    private String status;

    /** 残り日数（登録期限まで）。期限切れの場合は負数になり得る */
    private Long remainingDays;

    /** 期限切れフラグ（true=期限切れ） */
    private boolean expired;

    /** サムネイル画像URL（画像が無い場合は null） */
    private String thumbnailUrl;
}
