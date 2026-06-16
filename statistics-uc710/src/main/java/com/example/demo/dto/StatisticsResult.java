package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 統計画面に渡す集計結果のまとめ。
 * <p>全体指標（出品数・成約数・成約率）に加え、カテゴリ別・時系列の明細を保持する。</p>
 */
@Data
public class StatisticsResult {

    /** 出品数（期間内の items.created_at 件数） */
    private long listingCount;
    /** 成約数（期間内の transactions.completed_at 件数） */
    private long dealCount;
    /** 成約率（％）。出品数が0の場合は0（ゼロ除算回避） */
    private double dealRate;

    /** カテゴリ別集計 */
    private List<CategoryStat> categoryStats = new ArrayList<>();
    /** 時系列集計 */
    private List<TimeSeriesPoint> timeSeries = new ArrayList<>();

    /** 対象期間にデータが存在するか（false の場合は画面で「データなし」表示） */
    private boolean hasData;
}
