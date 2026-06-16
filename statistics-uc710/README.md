# UC-710 統計情報を閲覧する — 実装一式（引き継ぎ）

管理者向け統計画面（出品数・成約数・成約率／カテゴリ別棒グラフ／時系列折れ線グラフ）と、
検証用サンプルデータ投入をまとめた一式です。**コピペでそのまま動作**します。

## 生成ファイル（UC-710 の担当範囲）
| 種別 | パス | 役割 |
|---|---|---|
| Controller | `controller/AdminStatisticsController.java` | `GET /admin/statistics`。モック管理者認証＋管理者チェック |
| Service(IF) | `service/StatisticsService.java` | 集計の契約 |
| Service(Impl) | `service/impl/StatisticsServiceImpl.java` | 期間正規化・成約率計算（ゼロ除算回避）・時系列マージ |
| Repository | `repository/StatisticsRepository.java` | 集計SQL（PostgreSQL・JdbcTemplate） |
| Form | `form/StatisticsSearchForm.java` | 観点・期間・粒度 |
| DTO | `dto/StatisticsResult / CategoryStat / TimeSeriesPoint` | 画面表示用 |
| Entity | `entity/Users.java` | モック認証用（role=INTEGER, 1=管理者）※既存があればそちらを正に |
| View | `templates/statistics_view.html` | Thymeleaf＋Chart.js(CDN) |
| サンプルデータ | `config/TestDataInitializer.java` | 起動時にダミーデータ投入（CommandLineRunner） |

## 前提スキーマ（整合性の基準）
アップロードSQL（`free_market123`）に整合。**`role/status/type/condition` は INTEGER**、
`items.place`、`users.login_at/temporary_password/last_password_change` 列あり。

> 集計クエリは型ゆれのある列に依存せず、**`created_at / completed_at / category_id / name / item_id` のみ**を
> 参照するため、スキーマ差異に強い。ただし **サンプル投入（TestDataInitializer）は上記スキーマ前提**。
> VARCHAR系スキーマで動かす場合は INSERT の値（condition/type/status）と列（place 等）を調整すること。

## 集計ロジック
- **出品数** = `items.created_at` が期間内の件数
- **成約数** = `transactions.completed_at` が刻まれている（＝完了）かつ期間内の件数
- **成約率** = 成約数 / 出品数 × 100（出品数0なら0。ゼロ除算回避）
- 期間は `[開始日 00:00, 終了日+1日 00:00)` で終了日も含めて集計

## 動作確認
1. チームのスキーマSQLで `free_market123` を構築（テーブル作成済みにする）。
2. `application.properties` の接続先を環境に合わせる。
3. 起動（`./gradlew bootRun`）。`TestDataInitializer` が過去6ヶ月分のダミーを投入
   （items が30件以上あればスキップ）。
4. ブラウザで **`http://localhost:8080/admin/statistics`**
   - 観点を「カテゴリ別／時系列」に切替 → Chart.js のグラフが描画。
   - 対象期間にデータが無い場合は「該当するデータがありません」。

## 並行開発の結合ポイント（TODO）
- **ログイン**：`AdminStatisticsController#mockLoginUser()` が管理者(role=1)の仮ユーザを供給。
  結合時は本メソッドを削除し、セッションの実ユーザに差し替え。管理者でなければリダイレクト。
- **既存 Users エンティティ**：プロジェクトに既存があればそちらを使用し、本 `entity/Users.java` は破棄可。
- **サンプルデータ**：本番投入が不要なら `app.seed-test-data=false`。
