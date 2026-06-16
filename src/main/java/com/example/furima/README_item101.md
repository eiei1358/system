# item-101（トップ／物品一覧画面）実装 — 引き継ぎメモ

担当：item-101。並行開発のため、本画面の範囲外（他画面）コードは含みません。

## 生成物（このコミットの範囲）
| 種別 | クラス／ファイル | 役割 |
|---|---|---|
| Controller | `controller/Item101Controller` | `GET /` `GET /item-101` で一覧表示 |
| Service(IF) | `service/ItemListService` | 一覧・検索の契約（item-101 専用） |
| Service(Impl) | `service/impl/ItemListServiceImpl` | DTO組み立て・価格ラベル・残り日数算出 |
| Form | `form/ItemSearchForm` | 検索条件（keyword/category_id/status/sort） |
| DTO | `dto/ItemSummaryDto` | 一覧カード表示用 |
| 支援 | `support/LoginUserProvider` | セッションのログインユーザ取得（**現在ダミー**） |
| View | `resources/templates/item101.html` | Thymeleaf 画面 |
| CSS | `resources/static/css/item101.css` | 画面スタイル |
| Repository(共有) | `repository/ItemsRepository`,`CategoriesRepository`,`ItemImagesRepository` | データアクセス |
| Entity(共有) | `entity/Users`,`Items`,`Categories`,`ItemImages` | **クラス設計書(CSV)準拠** |

> Entity / Repository はチーム共有の「契約」部分です。既に他メンバーが生成済みなら**そちらを正**とし、本ファイルは破棄してください（重複定義の競合回避）。

## 契約（CSV）厳守事項
- クラス名は**複数形**：`Users / Items / Categories / ItemImages`（CSVのコンストラクタ定義準拠）。
- プロパティ名は**snake_case**：`item_id, user_id, category_id, created_at, image_url` 等。1文字も変更不可。
- 型：`Integer / String / Double / LocalDate(deadline) / LocalDateTime(created_at)`。
- Lombok：`@Data / @NoArgsConstructor / @AllArgsConstructor`。

## 並行開発の結合ポイント（TODO）
1. **ログイン機能**（別担当）：`LoginUserProvider` がダミーユーザを返している。認証実装後、
   セッション属性 `loginUser`（要名称調整）から取得し、未ログインはログイン画面へリダイレクト。
2. **他画面遷移**：サイドバー／「出品する」／詳細カードのリンクは `href="#"`（ダミー）。
   item-102(検索)/103(出品)/104(メッセージ)/106(マイページ)/107(物品詳細) 完成後に正式パスへ。
3. **共有 ItemService**（登録・成約等）：item-101 は閲覧のみのため未使用。責務分離済み。

## 必要な依存（pom.xml は共有のため本コミットでは生成せず）
チームの `pom.xml` に以下が含まれている前提です（無ければ追加を調整）。
- `spring-boot-starter-web`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-data-jpa`
- JDBCドライバ（構築済みDBに合わせる：MySQL 等）
- `org.projectlombok:lombok`

※ `jakarta.*` を使用（Spring Boot 3 系）。Spring Boot 2 系の場合は `javax.*` へ要置換。

## 動作確認
- アプリ起動後 `http://localhost:8080/`（または `/item-101`）。
- ログイン未実装でもダミーユーザで表示可。DB の `items/categories/item_images` を参照。
