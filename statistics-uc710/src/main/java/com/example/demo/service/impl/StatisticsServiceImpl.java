package com.example.demo.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.dto.CategoryStat;
import com.example.demo.dto.StatisticsResult;
import com.example.demo.dto.TimeSeriesPoint;
import com.example.demo.form.StatisticsSearchForm;
import com.example.demo.repository.StatisticsRepository;
import com.example.demo.service.StatisticsService;

/**
 * {@link StatisticsService} の実装。
 * <p>
 * 期間の正規化・集計の呼び出し・成約率の計算（ゼロ除算回避）・時系列のマージを行う。
 * </p>
 *
 * @author UC-710 担当
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    /** 時系列の粒度として許可する値（SQLインジェクション対策のホワイトリスト）。 */
    private static final String GRANULARITY_DAY = "day";
    private static final String GRANULARITY_MONTH = "month";

    private final StatisticsRepository statisticsRepository;

    /**
     * コンストラクタインジェクション。
     *
     * @param statisticsRepository 集計リポジトリ
     */
    public StatisticsServiceImpl(StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StatisticsResult getStatistics(StatisticsSearchForm form) {
        // --- 期間の既定値補完（未指定なら直近6ヶ月） ---
        LocalDate endDate = form.getEndDate() != null ? form.getEndDate() : LocalDate.now();
        LocalDate startDate = form.getStartDate() != null ? form.getStartDate() : endDate.minusMonths(6);

        // created_at / completed_at は TIMESTAMP。終了日を含めるため [start 0:00, end+1日 0:00) で範囲化
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        String granularity = normalizeGranularity(form.getGranularity());

        StatisticsResult result = new StatisticsResult();

        // --- 全体（出品数・成約数・成約率） ---
        long listingCount = statisticsRepository.countListings(start, end);
        long dealCount = statisticsRepository.countDeals(start, end);
        result.setListingCount(listingCount);
        result.setDealCount(dealCount);
        result.setDealRate(calcDealRate(dealCount, listingCount));

        // --- カテゴリ別 ---
        List<CategoryStat> categoryStats = statisticsRepository.aggregateByCategory(start, end);
        result.setCategoryStats(categoryStats);

        // --- 時系列（出品数・成約数をバケットでマージ） ---
        result.setTimeSeries(buildTimeSeries(start, end, granularity));

        // --- データ有無（出品も成約も0なら「データなし」） ---
        result.setHasData(listingCount > 0 || dealCount > 0);

        return result;
    }

    /**
     * 成約率（％）を計算する。出品数が0の場合はゼロ除算を避けて 0 を返す。
     *
     * @param dealCount    成約数
     * @param listingCount 出品数
     * @return 成約率（％、小数第1位まで丸め）
     */
    private double calcDealRate(long dealCount, long listingCount) {
        if (listingCount == 0) {
            return 0.0; // ゼロ除算回避
        }
        double rate = (double) dealCount / (double) listingCount * 100.0;
        return Math.round(rate * 10.0) / 10.0;
    }

    /**
     * 粒度をホワイトリストで正規化する（不正値は月別にフォールバック）。
     *
     * @param granularity 入力粒度
     * @return {@code "day"} または {@code "month"}
     */
    private String normalizeGranularity(String granularity) {
        return GRANULARITY_DAY.equals(granularity) ? GRANULARITY_DAY : GRANULARITY_MONTH;
    }

    /**
     * 出品数・成約数の時系列を同一の期間軸でマージする。
     *
     * @param start       期間開始
     * @param end         期間終了
     * @param granularity 粒度（month/day）
     * @return 期間ラベル昇順の時系列リスト
     */
    private List<TimeSeriesPoint> buildTimeSeries(LocalDateTime start, LocalDateTime end, String granularity) {
        DateTimeFormatter fmt = GRANULARITY_DAY.equals(granularity)
                ? DateTimeFormatter.ofPattern("MM/dd")
                : DateTimeFormatter.ofPattern("yyyy/MM");

        // バケット日時の昇順を保つため LinkedHashMap を使用
        Map<LocalDateTime, long[]> merged = new LinkedHashMap<>();

        for (Object[] row : statisticsRepository.listingsOverTime(start, end, granularity)) {
            LocalDateTime bucket = (LocalDateTime) row[0];
            long cnt = (Long) row[1];
            merged.computeIfAbsent(bucket, k -> new long[2])[0] += cnt;
        }
        for (Object[] row : statisticsRepository.dealsOverTime(start, end, granularity)) {
            LocalDateTime bucket = (LocalDateTime) row[0];
            long cnt = (Long) row[1];
            merged.computeIfAbsent(bucket, k -> new long[2])[1] += cnt;
        }

        // バケットを時刻順に並べ替えてラベル化
        List<TimeSeriesPoint> points = new ArrayList<>();
        merged.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> points.add(new TimeSeriesPoint(
                        e.getKey().format(fmt), e.getValue()[0], e.getValue()[1])));
        return points;
    }
}
