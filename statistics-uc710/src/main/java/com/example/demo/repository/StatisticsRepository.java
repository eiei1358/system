package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.CategoryStat;
import com.example.demo.dto.TimeSeriesPoint;

/**
 * UC-710 統計の集計クエリ（PostgreSQL ネイティブSQL）。
 * <p>
 * 集計は「日時・カテゴリ・件数」のみを参照し、スキーマ間で型ゆれのある
 * {@code status / type / condition / role} などの列には依存しない設計とする。
 * </p>
 * <ul>
 *   <li><b>出品数</b>：{@code items.created_at} が期間内の件数</li>
 *   <li><b>成約数</b>：{@code transactions.completed_at} が刻まれている（＝完了）かつ期間内の件数</li>
 * </ul>
 *
 * @author UC-710 担当
 */
@Repository
public class StatisticsRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * コンストラクタインジェクション。
     *
     * @param jdbcTemplate Spring が提供する JdbcTemplate
     */
    public StatisticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 期間内の出品数を取得する。
     *
     * @param start 期間開始（含む）
     * @param end   期間終了（含まない＝終了日の翌日0時）
     * @return 出品数
     */
    public long countListings(LocalDateTime start, LocalDateTime end) {
        Long c = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM items WHERE created_at >= ? AND created_at < ?",
                Long.class, start, end);
        return c != null ? c : 0L;
    }

    /**
     * 期間内の成約数を取得する（completed_at が刻まれている取引）。
     *
     * @param start 期間開始（含む）
     * @param end   期間終了（含まない）
     * @return 成約数
     */
    public long countDeals(LocalDateTime start, LocalDateTime end) {
        Long c = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM transactions "
                + "WHERE completed_at IS NOT NULL AND completed_at >= ? AND completed_at < ?",
                Long.class, start, end);
        return c != null ? c : 0L;
    }

    /**
     * カテゴリ別の出品数・成約数を取得する。
     * <p>出品が0件のカテゴリも 0 として返す（全カテゴリを左結合）。</p>
     *
     * @param start 期間開始（含む）
     * @param end   期間終了（含まない）
     * @return カテゴリ別集計（category_id 昇順）
     */
    public List<CategoryStat> aggregateByCategory(LocalDateTime start, LocalDateTime end) {
        String sql =
                "SELECT c.name AS category_name, "
              + "  COUNT(DISTINCT i.item_id) FILTER (WHERE i.created_at >= ? AND i.created_at < ?) AS listing_count, "
              + "  COUNT(DISTINCT t.transaction_id) FILTER (WHERE t.completed_at IS NOT NULL AND t.completed_at >= ? AND t.completed_at < ?) AS deal_count "
              + "FROM categories c "
              + "LEFT JOIN items i ON i.category_id = c.category_id "
              + "LEFT JOIN transactions t ON t.item_id = i.item_id "
              + "GROUP BY c.category_id, c.name "
              + "ORDER BY c.category_id";
        return jdbcTemplate.query(sql,
                (rs, n) -> new CategoryStat(
                        rs.getString("category_name"),
                        rs.getLong("listing_count"),
                        rs.getLong("deal_count")),
                start, end, start, end);
    }

    /**
     * 時系列の出品数を取得する。
     *
     * @param start       期間開始（含む）
     * @param end         期間終了（含まない）
     * @param granularity 粒度（{@code "month"} または {@code "day"}）
     * @return 期間バケットごとの (バケット開始日時, 件数)。SQLは date_trunc を使用。
     */
    public List<Object[]> listingsOverTime(LocalDateTime start, LocalDateTime end, String granularity) {
        // date_trunc の第1引数はバインドできない（PostgreSQL/H2 とも）。
        // granularity はホワイトリスト（month/day）済みのため直接埋め込む。
        String field = safeField(granularity);
        String sql =
                "SELECT date_trunc('" + field + "', created_at) AS bucket, COUNT(*) AS cnt "
              + "FROM items WHERE created_at >= ? AND created_at < ? "
              + "GROUP BY bucket ORDER BY bucket";
        return jdbcTemplate.query(sql,
                (rs, n) -> new Object[]{ rs.getTimestamp("bucket").toLocalDateTime(), rs.getLong("cnt") },
                start, end);
    }

    /**
     * date_trunc に渡す粒度を安全な値（{@code month}/{@code day}）に限定する。
     *
     * @param granularity 入力粒度
     * @return {@code "day"} または {@code "month"}
     */
    private String safeField(String granularity) {
        return "day".equals(granularity) ? "day" : "month";
    }

    /**
     * 時系列の成約数を取得する。
     *
     * @param start       期間開始（含む）
     * @param end         期間終了（含まない）
     * @param granularity 粒度（{@code "month"} または {@code "day"}）
     * @return 期間バケットごとの (バケット開始日時, 件数)。
     */
    public List<Object[]> dealsOverTime(LocalDateTime start, LocalDateTime end, String granularity) {
        String field = safeField(granularity);
        String sql =
                "SELECT date_trunc('" + field + "', completed_at) AS bucket, COUNT(*) AS cnt "
              + "FROM transactions WHERE completed_at IS NOT NULL AND completed_at >= ? AND completed_at < ? "
              + "GROUP BY bucket ORDER BY bucket";
        return jdbcTemplate.query(sql,
                (rs, n) -> new Object[]{ rs.getTimestamp("bucket").toLocalDateTime(), rs.getLong("cnt") },
                start, end);
    }
}
