package com.example.demo.service;

import com.example.demo.dto.StatisticsResult;
import com.example.demo.form.StatisticsSearchForm;

/**
 * UC-710 統計情報閲覧のサービス。
 * <p>Controller はこのインターフェースにのみ依存する。</p>
 */
public interface StatisticsService {

    /**
     * 検索・集計条件に基づいて統計結果を集計する。
     *
     * @param form 集計条件（観点・期間・粒度）。期間未指定の場合は実装側で既定値を補完する
     * @return 集計結果（出品数・成約数・成約率・カテゴリ別・時系列）。データが無い場合は
     *         {@link StatisticsResult#isHasData()} が false になる
     */
    StatisticsResult getStatistics(StatisticsSearchForm form);
}
