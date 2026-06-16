package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カテゴリ別の集計結果（1カテゴリ＝1行）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStat {
    /** カテゴリ名 */
    private String categoryName;
    /** 出品数（期間内に出品された件数） */
    private long listingCount;
    /** 成約数（期間内に成約した件数） */
    private long dealCount;
}
