# フリマ社内システム

Spring Boot + React（統合） + PostgreSQL による完全なフルスタックフリマシステム

## Eclipse へのインポート

### 方法1: ZIP ファイルから直接インポート

1. このリポジトリを ZIP ダウンロード、または `git clone` で取得
2. Eclipse を起動
3. **File → Import → General → Projects from Folder or Archive**
4. ZIP ファイルを選択（または、フォルダを選択）
5. **Finish**

### 方法2: Git Clone してから Import

```bash
git clone <リポジトリURL> free-market
cd free-market
```

Eclipse:
1. **File → Import → Gradle → Existing Gradle Project**
2. リポジトリのルートディレクトリを選択
3. **Finish**

## ビルド & 実行

### 🚀 最も簡単な実行方法

ZIP をインポート後、以下のファイルをダブルクリック：

- **Windows**: `run.bat`
- **Mac/Linux**: `run.sh`

アプリケーションが自動的に起動し、以下にアクセスできます：
- フロントエンド: `http://localhost:8080/`
- ログイン: `user0@sample.com` / `pass`

### Eclipse での実行

1. プロジェクトをインポート後、Gradle を更新:
   - プロジェクトを右クリック → **Gradle** → **Refresh Gradle Project**

2. Spring Boot を実行:
   - プロジェクトを右クリック → **Run As** → **Java Application**
   - Main class: `FreeMarketBackendApplication` を選択

3. ブラウザでアクセス:
   - `http://localhost:8080/`

### コマンドラインでの実行

#### 開発モード（H2 メモリデータベース）

```bash
# Windows
run.bat

# Mac/Linux
./run.sh

# または直接実行
./gradlew bootRun --args="--spring.profiles.active=dev"
```

#### 本番モード（PostgreSQL）

```bash
# ビルド
./gradlew clean bootJar

# 実行（PostgreSQL が必要）
java -jar build/libs/free-market-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 本番デプロイ（AlmaLinux）

### 1. 前提条件

```bash
# Java 17
sudo dnf install -y java-17-openjdk java-17-openjdk-devel

# Node.js
sudo dnf install -y nodejs npm

# PostgreSQL
sudo dnf install -y postgresql-server postgresql-contrib

# Nginx
sudo dnf install -y nginx
```

### 2. PostgreSQL セットアップ

```bash
sudo postgresql-setup initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql

# DB 初期化
sudo -u postgres psql <<EOF
CREATE ROLE student PASSWORD 'himitu' LOGIN;
CREATE DATABASE free_market123 OWNER student;
\c free_market123
EOF
```

初期スキーマを実行:
```bash
sudo -u postgres psql -d free_market123 -f src/main/resources/frontend/schema.sql
```

### 3. ビルド & デプロイ

```bash
# ビルド
npm install --prefix src/main/resources/frontend --legacy-peer-deps
./gradlew clean bootJar

# インストール
sudo mkdir -p /opt/furima
sudo cp build/libs/free-market-backend-0.0.1-SNAPSHOT.jar /opt/furima/app.jar
sudo chown -R furima:furima /opt/furima
```

### 4. Systemd サービス設定

`/etc/systemd/system/furima.service`:
```ini
[Unit]
Description=Free Market Backend
After=network.target postgresql.service

[Service]
Type=simple
User=furima
WorkingDirectory=/opt/furima
ExecStart=/usr/bin/java -jar /opt/furima/app.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

起動:
```bash
sudo systemctl daemon-reload
sudo systemctl enable furima
sudo systemctl start furima
```

### 5. Nginx 設定

`/etc/nginx/conf.d/furima.conf`:
```nginx
upstream app {
    server 127.0.0.1:8080;
}

server {
    listen 80;
    client_max_body_size 50M;

    location / {
        proxy_pass http://app;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

再起動:
```bash
sudo systemctl restart nginx
```

## アクセス

### 開発環境（H2 使用時）

- フロントエンド: `http://localhost:8080/`
- H2 コンソール: `http://localhost:8080/api/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - ユーザー名: `sa`
  - パスワード: (空)

### 本番環境（PostgreSQL 使用時）

```
http://<server-ip>/
```

**デフォルト認証情報**:
- 社員: user0@sample.com / pass
- 管理者: admin@sample.com / pass

## 実装機能

✅ ユーザー認証・ログイン履歴・連続失敗ロック  
✅ CSVインポート・人事管理  
✅ 物品登録・複数画像・検索・フィルタリング  
✅ 先着順応募・オークション機能  
✅ メッセージ・当事者間チャット  
✅ 2ステップ譲渡完了・QRコード  
✅ NGワード監視・メッセージ通報  
✅ 管理者監視・統計ダッシュボード  

## API エンドポイント

すべてのエンドポイントは `/api` で始まります。

| メソッド | エンドポイント | 説明 |
|---------|---|---|
| POST | `/api/auth/login` | ログイン |
| POST | `/api/auth/password-change` | パスワード変更 |
| GET | `/api/users/{userId}` | ユーザー情報取得 |
| GET | `/api/items/search` | 物品検索 |
| POST | `/api/items` | 物品登録 |
| POST | `/api/applications` | 応募 |
| GET | `/api/transactions/{id}` | 取引取得 |
| POST | `/api/messages` | メッセージ送信 |
| GET | `/api/dashboard/stats` | 統計情報 |

## トラブルシューティング

### ビルド失敗時

```bash
# Gradle キャッシュ削除
rm -rf .gradle build/

# 再ビルド
./gradlew clean bootJar
```

### Node.js エラー

```bash
# npm キャッシュ削除
npm cache clean --force

# 再インストール
npm install --prefix src/main/resources/frontend --legacy-peer-deps
```

### ポート競合

```bash
# 8080 ポート確認
lsof -i :8080

# application.properties で変更
server.port=8081
```

## ライセンス

プライベート社内システム

## サポート

問題が発生した場合:

```bash
# ログ確認
sudo journalctl -u furima -f

# Gradle デバッグビルド
./gradlew clean bootJar --debug
```
