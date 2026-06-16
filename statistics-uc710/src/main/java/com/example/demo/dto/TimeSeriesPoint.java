package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 時系列集計の1点（1期間＝1点）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesPoint {
    /** 期間ラベル（例: 月別 "2026/03"、日別 "03/15"） */
    private String label;
    /** その期間の出品数 */
    private long listingCount;
    /** その期間の成約数 */
    private long dealCount;
}
