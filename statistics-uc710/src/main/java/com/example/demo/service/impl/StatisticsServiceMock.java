package com.example.demo.service.impl;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CategoryStat;
import com.example.demo.dto.StatisticsResult;
import com.example.demo.dto.TimeSeriesPoint;
import com.example.demo.form.StatisticsSearchForm;
import com.example.demo.service.StatisticsService;

/**
 * {@link StatisticsService} の <b>モック（DB不要）実装</b>。
 * <p>
 * <b>【動作確認用】データベースを用意しなくても統計画面・グラフを表示できるようにするための実装。</b><br>
 * {@code app.use-mock-statistics=true}（既定）かつ {@code @Primary} のため、
 * DBアクセス版（{@link StatisticsServiceImpl}）より優先して使用される。
 * </p>
 *
 * <pre>
 * // 本物のDB集計で動かす場合は application.properties で
 * //   app.use-mock-statistics=false
 * // とする（PostgreSQL の free_market123 が必要）。
 * </pre>
 *
 * @author UC-710 担当
 */
@Service
@Primary
@ConditionalOnProperty(name = "app.use-mock-statistics", havingValue = "true", matchIfMissing = true)
public class StatisticsServiceMock implements StatisticsService {

    /**
     * {@inheritDoc}
     * <p>
     * 期間・観点に関わらず、グラフ確認用の固定ダミーデータを返す
     * （カテゴリ偏り・時系列の右肩上がり・成約率を再現）。
     * </p>
     */
    @Override
    public StatisticsResult getStatistics(StatisticsSearchForm form) {
        StatisticsResult r = new StatisticsResult();

        // --- 全体 ---
        r.setListingCount(46);
        r.setDealCount(27);
        r.setDealRate(58.7); // 27 / 46 * 100

        // --- カテゴリ別（IT機器 ＞ スマホ ＞ 周辺機器 の偏り） ---
        r.setCategoryStats(Arrays.asList(
                new CategoryStat("IT機器", 23, 17),
                new CategoryStat("スマホ", 15, 8),
                new CategoryStat("周辺機器", 8, 2)));

        // --- 時系列（右肩上がり） ---
        r.setTimeSeries(Arrays.asList(
                new TimeSeriesPoint("2026/01", 4, 2),
                new TimeSeriesPoint("2026/02", 5, 2),
                new TimeSeriesPoint("2026/03", 7, 2),
                new TimeSeriesPoint("2026/04", 8, 7),
                new TimeSeriesPoint("2026/05", 10, 7),
                new TimeSeriesPoint("2026/06", 12, 7)));

        r.setHasData(true);
        return r;
    }
}
