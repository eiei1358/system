package com.example.demo.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

/**
 * UC-710 統計情報閲覧の検索・集計条件フォーム。
 * <p>集計の観点（全体／カテゴリ別／時系列）と対象期間を保持する。</p>
 */
@Data
public class StatisticsSearchForm {

    /**
     * 集計の観点。
     * <ul>
     *   <li>{@code "overall"}    … 全体（出品数・成約数・成約率）</li>
     *   <li>{@code "category"}   … カテゴリ別</li>
     *   <li>{@code "timeseries"} … 時系列（月別／日別）</li>
     * </ul>
     */
    private String viewType = "overall";

    /** 集計期間 開始日 */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    /** 集計期間 終了日（当日を含めて集計する） */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    /**
     * 時系列の粒度。
     * <ul>
     *   <li>{@code "month"} … 月別（既定）</li>
     *   <li>{@code "day"}   … 日別</li>
     * </ul>
     */
    private String granularity = "month";

    /**
     * グラフの種別（参考実装に合わせたトグル）。
     * <ul>
     *   <li>{@code "bar"}  … 棒グラフ（既定）</li>
     *   <li>{@code "line"} … 折れ線グラフ</li>
     * </ul>
     */
    private String graphType = "bar";
}
