# フリマ社内システム

Spring Boot + React（統合） + PostgreSQL による完全なフルスタックフリマシステム

**Eclipse への統合**: フロントエンドは Spring Boot バックエンドに統合されているため、バックエンドプロジェクトのみをインポートすれば完了。

## システム要件

- AlmaLinux 8/9（CentOS互換）
- Java 17以上
- Node.js 18以上（Gradle が自動ダウンロード）
- PostgreSQL 12以上
- Nginx

## インストール手順

### 1. システム依存関係のインストール（AlmaLinuxサーバー）

```bash
# パッケージマネージャーの更新
sudo dnf update -y

# Java 17のインストール
sudo dnf install -y java-17-openjdk java-17-openjdk-devel

# Node.js と npm のインストール
sudo dnf install -y nodejs npm

# PostgreSQL のインストール
sudo dnf install -y postgresql-server postgresql-contrib

# Nginx のインストール
sudo dnf install -y nginx

# Git のインストール（オプション）
sudo dnf install -y git
```

### 2. PostgreSQL の初期化と設定

```bash
# PostgreSQL の初期化
sudo postgresql-setup initdb

# PostgreSQL サービスの開始と有効化
sudo systemctl start postgresql
sudo systemctl enable postgresql

# データベースと社員ユーザーの作成
sudo -u postgres psql <<EOF
DROP DATABASE IF EXISTS free_market123;
DROP ROLE IF EXISTS student;
CREATE ROLE student PASSWORD 'himitu' LOGIN;
CREATE DATABASE free_market123 OWNER student;
\c free_market123
-- スキーマ初期化スクリプト（同梱の init.sql の内容を実行）
EOF
```

### 3. データベーススキーマの作成

```bash
sudo -u postgres psql -d free_market123 -f infra/database/init.sql
```

### 4. アプリケーションのデプロイ

リポジトリをクローン（または配置）:

```bash
cd /tmp
git clone <リポジトリURL> free-market
cd free-market
```

デプロイスクリプトを実行:

```bash
chmod +x deploy.sh
sudo ./deploy.sh
```

デプロイスクリプトは以下を自動実行します:
- Node.js 依存関係のインストール
- Spring Boot + React 統合ビルド
- Nginx の設定と再起動
- Systemd サービスの登録と起動

### 5. 環境変数の設定

バックエンド環境ファイルを編集:

```bash
sudo vim /opt/furima/backend/.env
```

以下の項目を環境に合わせて設定:

```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

設定後、サービスを再起動:

```bash
sudo systemctl restart furima-backend
```

## 起動・停止

### サービス開始

```bash
sudo systemctl start furima-backend
sudo systemctl start nginx
```

### サービス停止

```bash
sudo systemctl stop furima-backend
sudo systemctl stop nginx
```

### ステータス確認

```bash
sudo systemctl status furima-backend
sudo systemctl status nginx
```

### ログ確認

```bash
# バックエンド + フロントエンド ログ
sudo journalctl -u furima-backend -f

# Nginx ログ
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log
```

## アクセス

アプリケーションには以下のURLでアクセス:

```
http://<server-ip>/
```

### デフォルトログイン情報

- **社員**: user0@sample.com / pass
- **管理者**: admin@sample.com / pass

## 開発

### ローカル開発環境のセットアップ

#### バックエンド + フロントエンド開発

**Eclipse へのインポート:**

1. Eclipse を起動
2. File → Import → Gradle → Existing Gradle Project
3. `backend` ディレクトリを選択
4. Finish

フロントエンド は `backend/src/main/resources/frontend` に統合されています。

#### 開発サーバーの実行

**バックエンド:**

```bash
cd backend
./gradlew bootRun
```

**フロントエンド（別ターミナル）:**

```bash
cd backend/src/main/resources/frontend
npm install
npm run dev
```

フロントエンドは `http://localhost:5173` で起動。API は `http://localhost:8080/api` にプロキシされます。

#### ビルド

```bash
cd backend
./gradlew clean bootJar
```

完成した JAR は `backend/build/libs/free-market-backend-0.0.1-SNAPSHOT.jar`

## プロジェクト構成

```
system/
├── backend/                                     # Spring Boot プロジェクト（メイン）
│   ├── src/main/java/com/example/furima/
│   │   ├── entity/                             # 9つのJPAエンティティ
│   │   ├── repository/                         # 9つのリポジトリ
│   │   ├── service/                            # 7つのサービス
│   │   ├── controller/                         # 8つのコントローラー
│   │   │   ├── FrontendController.java         # SPA ルーティング
│   │   │   └── ...
│   │   ├── dto/                                # データ転送オブジェクト
│   │   └── config/                             # シード初期化
│   ├── src/main/resources/
│   │   ├── frontend/                           # React フロントエンド（統合）
│   │   │   ├── src/
│   │   │   │   ├── pages/                      # 8ページコンポーネント
│   │   │   │   ├── api.ts                      # APIクライアント
│   │   │   │   └── main.tsx
│   │   │   ├── package.json
│   │   │   └── vite.config.ts
│   │   ├── static/                             # React ビルド成果物（自動生成）
│   │   └── application.properties              # Spring Boot 設定
│   ├── build.gradle                            # Gradle 設定（Node.js ビルド統合）
│   └── gradlew                                 # Gradle Wrapper
│
├── infra/                                      # インフラ設定
│   ├── nginx/furima.conf                       # リバースプロキシ設定
│   ├── systemd/furima-backend.service          # Systemd サービス定義
│   └── database/init.sql                       # スキーマ
│
├── deploy.sh                                   # 完全自動デプロイスクリプト
└── README.md                                   # このファイル
```

## API エンドポイント

すべてのエンドポイントは `/api` で始まります。

### 認証
- `POST /api/auth/login` - ログイン
- `POST /api/auth/password-change` - パスワード変更
- `POST /api/auth/password-reset-request` - パスワードリセット

### ユーザー
- `GET /api/users/{userId}` - ユーザー情報取得
- `POST /api/users/csv-import` - CSVインポート
- `PUT /api/users/{userId}/status` - ユーザーステータス更新
- `PUT /api/users/{userId}/info` - ユーザー情報更新

### 物品
- `POST /api/items` - 物品作成
- `GET /api/items/{itemId}` - 物品取得
- `PUT /api/items/{itemId}` - 物品更新
- `GET /api/items/search` - 物品検索
- `POST /api/items/{itemId}/extend-deadline` - 期限延長
- `POST /api/items/{itemId}/report` - 物品報告

### 応募
- `POST /api/applications` - 応募作成
- `POST /api/applications/{itemId}/finalize-auction` - オークション確定
- `GET /api/applications/item/{itemId}` - 物品への応募一覧
- `GET /api/applications/user/{userId}` - ユーザーの応募一覧

### 取引
- `GET /api/transactions/{transactionId}` - 取引取得
- `GET /api/transactions/code/{transferCode}` - 譲渡コードで取引取得
- `POST /api/transactions/{transactionId}/seller-complete` - 出品者完了
- `POST /api/transactions/{transactionId}/buyer-complete` - 購入者完了
- `POST /api/transactions/{transactionId}/generate-transfer-code` - 譲渡コード生成

### メッセージ
- `POST /api/messages` - メッセージ送信
- `GET /api/messages/thread` - メッセージスレッド取得
- `POST /api/messages/{messageId}/report` - メッセージ報告
- `DELETE /api/messages/{messageId}` - メッセージ削除

### ダッシュボード
- `GET /api/dashboard/stats` - 統計情報取得
- `GET /api/dashboard/condition-breakdown` - 状態別統計
- `GET /api/dashboard/type-breakdown` - タイプ別統計
- `GET /api/dashboard/recent-transactions` - 最近の取引
- `GET /api/dashboard/user-activity` - ユーザー活動統計

## 機能一覧

### 1. ユーザー認証（要件1, 4, 5, 6）
- ログイン/ログアウト
- パスワード変更（強制・自発的）
- 連続ログイン失敗によるアカウントロック
- 仮パスワード自動生成とメール送信

### 2. 人事管理（要件2, 3）
- CSVインポートによる一括社員登録
- 管理者によるパスワードリセット
- ユーザーステータス管理

### 3. 物品管理（要件7, 8, 9）
- 物品登録（写真付き）
- 多角的検索・フィルタリング
- 期限延長機能
- NGワード自動フィルタ

### 4. 応募・オークション（要件10, 11, 12）
- 無償・有償物品の先着順応募
- オークション方式（自動期限確定、最高入札者自動選定）
- メッセージによる当事者間チャット
- メール通知

### 5. 2ステップ譲渡完了（要件12, 17）
- 成約メール自動送信
- 出品者・購入者の二段階確認
- QRコード表示
- スマホ対応の状況確認画面

### 6. 管理者監視（要件13, 14, 15, 16）
- NGワードフィルタリング
- メッセージ・物品通報機能
- 管理画面での強制削除
- 利用統計ダッシュボード

## トラブルシューティング

### Spring Boot サービスが起動しない

```bash
# ログを確認
sudo journalctl -u furima-backend -n 100

# 環境変数を確認
sudo cat /opt/furima/backend/.env

# PostgreSQL が起動しているか確認
sudo systemctl status postgresql
```

### フロントエンドが表示されない

```bash
# Nginx が起動しているか確認
sudo systemctl status nginx

# Nginx 設定をテスト
sudo nginx -t

# Spring Boot が正しく起動しているか確認
sudo systemctl status furima-backend
```

### メール送信が失敗する

- MAIL_USERNAME と MAIL_PASSWORD を確認
- メールプロバイダの設定を確認
- ファイアウォールで SMTP ポート (587) が開いているか確認

## ライセンス

このプロジェクトはプライベートな社内システムです。

## サポート

問題が発生した場合は、以下のログを確認してください:

```bash
# バックエンド + フロントエンド ログ
sudo journalctl -u furima-backend -f

# Nginx エラー ログ
sudo tail -f /var/log/nginx/error.log

# PostgreSQL ログ
sudo tail -f /var/log/postgresql/*.log
```
