package com.example.demo.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * UC-710 統計の検証用サンプルデータ投入（起動時に1回実行）。
 * <p>
 * 統計画面のグラフ（時系列の推移・カテゴリの偏り・成約率）が綺麗に確認できるよう、
 * 過去6ヶ月にわたる items / transactions のダミーデータを生成する。
 * </p>
 * <ul>
 *   <li><b>時系列の変動</b>：月が進むほど出品数が増える「右肩上がり」</li>
 *   <li><b>カテゴリの偏り</b>：IT機器(多) ＞ スマホ(中) ＞ 周辺機器(少)</li>
 *   <li><b>成約率の検証</b>：カテゴリごとに成約率を変え（IT機器は高、周辺機器は低）、
 *       成約済み(completed_at有) と 未成約 を混在させる</li>
 * </ul>
 * <p>
 * {@code app.seed-test-data=false} で無効化できる。既に十分なデータがある場合はスキップする（冪等）。<br>
 * 実スキーマ（INTEGER型の status/type/condition、items.place、users の login_at 等）に整合させて投入する。
 * </p>
 *
 * @author UC-710 担当
 */
@Component
@ConditionalOnProperty(name = "app.seed-test-data", havingValue = "true", matchIfMissing = true)
public class TestDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(TestDataInitializer.class);

    /** 既にこの件数以上の items があれば投入をスキップする閾値。 */
    private static final int SKIP_THRESHOLD = 30;

    /** カテゴリ定義：名称・出品の重み・成約率。 */
    private record Cat(String name, int weight, double dealRate) {}

    private static final List<Cat> CATEGORIES = List.of(
            new Cat("IT機器", 50, 0.70),   // 出品多・成約率高
            new Cat("スマホ", 35, 0.45),   // 出品中・成約率中
            new Cat("周辺機器", 15, 0.20)  // 出品少・成約率低
    );

    private final JdbcTemplate jdbc;
    private final Random random = new Random(710); // 再現性のため固定シード

    public TestDataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 起動時に実行され、サンプルデータを投入する。
     *
     * @param args 起動引数（未使用）
     */
    @Override
    public void run(String... args) {
        try {
            Integer itemCount = jdbc.queryForObject("SELECT COUNT(*) FROM items", Integer.class);
            if (itemCount != null && itemCount >= SKIP_THRESHOLD) {
                log.info("[TestDataInitializer] items が既に {} 件あるため投入をスキップします。", itemCount);
                return;
            }

            List<Integer> userIds = ensureUsers();
            List<Integer> categoryIds = ensureCategories();
            int created = seedItemsAndTransactions(userIds, categoryIds);
            log.info("[TestDataInitializer] 検証用サンプルデータを投入しました（items {} 件）。", created);
        } catch (Exception e) {
            // DB未接続やスキーマ未作成でも起動を止めないようにする
            log.warn("[TestDataInitializer] サンプルデータ投入をスキップしました: {}", e.getMessage());
        }
    }

    /**
     * 社員ユーザを最低4名用意し、その user_id 一覧を返す。
     *
     * @return user_id のリスト
     */
    private List<Integer> ensureUsers() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        if (count == null || count < 4) {
            String sql = "INSERT INTO users "
                    + "(name, email, password, role, department, status, created_at, icon_url, "
                    + " login_at, temporary_password, last_password_change) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String[][] people = {
                    {"山田 太郎", "taro@sample.com", "0", "開発部"},
                    {"佐藤 花子", "hanako@sample.com", "0", "営業部"},
                    {"鈴木 次郎", "jiro@sample.com", "0", "総務部"},
                    {"管理者 花子", "admin@sample.com", "1", "管理部"},
            };
            LocalDateTime now = LocalDateTime.now();
            for (String[] p : people) {
                jdbc.update(sql, p[0], p[1], dummyHash(), Integer.parseInt(p[2]), p[3],
                        0, now, null, now, false, now);
            }
        }
        return jdbc.queryForList("SELECT user_id FROM users ORDER BY user_id", Integer.class);
    }

    /**
     * 統計用カテゴリ（IT機器／スマホ／周辺機器）を用意し、その category_id 一覧を返す。
     *
     * @return CATEGORIES と同順の category_id リスト
     */
    private List<Integer> ensureCategories() {
        List<Integer> ids = new ArrayList<>();
        for (Cat cat : CATEGORIES) {
            Integer id = jdbc.query(
                    "SELECT category_id FROM categories WHERE name = ? ORDER BY category_id LIMIT 1",
                    rs -> rs.next() ? rs.getInt(1) : null, cat.name());
            if (id == null) {
                id = jdbc.queryForObject(
                        "INSERT INTO categories (name) VALUES (?) RETURNING category_id",
                        Integer.class, cat.name());
            }
            ids.add(id);
        }
        return ids;
    }

    /**
     * 過去6ヶ月分の items と、一部の transactions（成約）を生成する。
     *
     * @param userIds     出品者・購入者に使う user_id
     * @param categoryIds CATEGORIES と同順の category_id
     * @return 生成した items 件数
     */
    private int seedItemsAndTransactions(List<Integer> userIds, List<Integer> categoryIds) {
        // 月が進むほど多くする（右肩上がり）：[-5ヶ月 .. 当月]
        int[] monthlyCounts = {4, 5, 7, 8, 10, 12};
        LocalDateTime now = LocalDateTime.now();
        YearMonth current = YearMonth.now();
        int total = 0;

        for (int m = 0; m < monthlyCounts.length; m++) {
            YearMonth ym = current.minusMonths(monthlyCounts.length - 1 - m);
            int count = monthlyCounts[m];

            for (int k = 0; k < count; k++) {
                int catIdx = weightedCategoryIndex();
                Cat cat = CATEGORIES.get(catIdx);
                Integer categoryId = categoryIds.get(catIdx);

                // 出品日時：その月内のランダムな日・時刻（ただし未来にはしない）
                LocalDateTime createdAt = randomDateTimeInMonth(ym);
                if (createdAt.isAfter(now)) {
                    createdAt = now.minusHours(1 + random.nextInt(24));
                }

                Integer sellerId = userIds.get(random.nextInt(userIds.size()));
                double price = 1000 + random.nextInt(99) * 500; // 1,000〜50,000
                int condition = random.nextInt(4); // 0:新品 1:新品同様 2:中古 3:ジャンク（INTEGER）
                int type = random.nextInt(3);      // 0:無償 1:有償 2:オークション（INTEGER）
                int place = random.nextInt(2);     // 0:神田 1:横浜（INTEGER）
                LocalDate deadline = createdAt.toLocalDate().plusDays(30);

                // items は status INTEGER（0:出品中 等）。成約有無は transactions 側で表現する。
                Integer itemId = jdbc.queryForObject(
                        "INSERT INTO items "
                        + "(user_id, name, description, condition, price, type, status, category_id, deadline, created_at, place) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING item_id",
                        Integer.class,
                        sellerId, cat.name() + " 出品No." + (total + 1), "サンプル出品データ",
                        condition, price, type, 0, categoryId, deadline, createdAt, place);

                // カテゴリ別の成約率に従って成約（completed_at を刻む）
                if (random.nextDouble() < cat.dealRate()) {
                    Integer buyerId = pickDifferent(userIds, sellerId);
                    LocalDateTime completedAt = createdAt.plusDays(1 + random.nextInt(10));
                    if (completedAt.isAfter(now)) {
                        completedAt = now.minusHours(random.nextInt(12));
                    }
                    // transactions.status INTEGER（1:完了）。completed_at が成約日時。
                    jdbc.update(
                            "INSERT INTO transactions (item_id, seller_id, buyer_id, status, completed_at) "
                            + "VALUES (?, ?, ?, ?, ?)",
                            itemId, sellerId, buyerId, 1, completedAt);
                } else if (random.nextDouble() < 0.3) {
                    // 一部は「取引中（未成約）」として completed_at = NULL の取引を残す
                    Integer buyerId = pickDifferent(userIds, sellerId);
                    jdbc.update(
                            "INSERT INTO transactions (item_id, seller_id, buyer_id, status, completed_at) "
                            + "VALUES (?, ?, ?, ?, NULL)",
                            itemId, sellerId, buyerId, 0);
                }
                total++;
            }
        }
        return total;
    }

    /**
     * 重み付きでカテゴリのインデックスを選ぶ。
     *
     * @return CATEGORIES のインデックス
     */
    private int weightedCategoryIndex() {
        int totalWeight = CATEGORIES.stream().mapToInt(Cat::weight).sum();
        int r = random.nextInt(totalWeight);
        int acc = 0;
        for (int i = 0; i < CATEGORIES.size(); i++) {
            acc += CATEGORIES.get(i).weight();
            if (r < acc) {
                return i;
            }
        }
        return CATEGORIES.size() - 1;
    }

    /**
     * 指定した年月内のランダムな日時を返す。
     *
     * @param ym 年月
     * @return その月内の日時
     */
    private LocalDateTime randomDateTimeInMonth(YearMonth ym) {
        int day = 1 + random.nextInt(ym.lengthOfMonth());
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        return ym.atDay(day).atTime(hour, minute);
    }

    /**
     * seller と異なる user_id を1つ選ぶ。
     *
     * @param userIds  user_id 一覧
     * @param sellerId 出品者
     * @return 出品者以外の user_id（候補が無ければ sellerId）
     */
    private Integer pickDifferent(List<Integer> userIds, Integer sellerId) {
        if (userIds.size() <= 1) {
            return sellerId;
        }
        Integer buyer;
        do {
            buyer = userIds.get(random.nextInt(userIds.size()));
        } while (buyer.equals(sellerId));
        return buyer;
    }

    /**
     * password 列（CHAR(64)）用のダミーハッシュ（64文字）を返す。
     *
     * @return 64文字のダミー文字列
     */
    private String dummyHash() {
        return "0".repeat(64);
    }
}
