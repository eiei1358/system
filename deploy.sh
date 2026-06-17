#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR/frontend"
INFRA_DIR="$SCRIPT_DIR/infra"

INSTALL_DIR="/opt/furima"
BACKEND_INSTALL_DIR="$INSTALL_DIR/backend"
FRONTEND_INSTALL_DIR="$INSTALL_DIR/frontend"

echo "=== フリマ社内システム デプロイスクリプト ==="
echo ""

echo "Step 1: システム依存関係の確認"
if ! command -v java &> /dev/null; then
    echo "Java 17のインストールが必要です"
    echo "実行: sudo dnf install java-17-openjdk java-17-openjdk-devel"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo "Node.jsのインストールが必要です"
    echo "実行: sudo dnf install nodejs npm"
    exit 1
fi

if ! command -v gradle &> /dev/null; then
    echo "Gradleのセットアップを行います..."
    cd "$BACKEND_DIR"
    if [ ! -f "gradlew" ]; then
        echo "gradle wrapper をセットアップ中..."
        gradle wrapper
    fi
    cd - > /dev/null
fi

echo "✓ システム依存関係の確認完了"
echo ""

echo "Step 2: Spring Boot JARファイルのビルド"
cd "$BACKEND_DIR"
echo "Gradleでビルド中..."
chmod +x gradlew
./gradlew clean bootJar
if [ ! -f "build/libs/free-market-backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "✗ JARファイルのビルドに失敗しました"
    exit 1
fi
echo "✓ Spring Boot JARファイルのビルド完了"
cd - > /dev/null
echo ""

echo "Step 3: Reactフロントエンドのビルド"
cd "$FRONTEND_DIR"
echo "npm依存関係をインストール中..."
npm install --legacy-peer-deps
echo "フロントエンドをビルド中..."
npm run build
if [ ! -d "dist" ]; then
    echo "✗ フロントエンドのビルドに失敗しました"
    exit 1
fi
echo "✓ Reactフロントエンドのビルド完了"
cd - > /dev/null
echo ""

echo "Step 4: インストールディレクトリの準備"
sudo mkdir -p "$INSTALL_DIR"
sudo mkdir -p "$BACKEND_INSTALL_DIR"
sudo mkdir -p "$FRONTEND_INSTALL_DIR"
sudo mkdir -p /var/www/furima/frontend

echo "✓ インストールディレクトリを作成"
echo ""

echo "Step 5: ファイルの配置"
echo "バックエンド JARファイルをコピー中..."
sudo cp "$BACKEND_DIR/build/libs/free-market-backend-0.0.1-SNAPSHOT.jar" "$BACKEND_INSTALL_DIR/"

echo "フロントエンド静的ファイルをコピー中..."
sudo cp -r "$FRONTEND_DIR/dist"/* /var/www/furima/frontend/

echo "✓ ファイルの配置完了"
echo ""

echo "Step 6: Systemdサービスの設定"
sudo cp "$INFRA_DIR/systemd/furima-backend.service" /etc/systemd/system/

echo "サービスファイルをリロード中..."
sudo systemctl daemon-reload

echo "サービスを有効化中..."
sudo systemctl enable furima-backend

echo "✓ Systemdサービスの設定完了"
echo ""

echo "Step 7: Nginx設定"
sudo cp "$INFRA_DIR/nginx/furima.conf" /etc/nginx/conf.d/

echo "Nginx設定をテスト中..."
if ! sudo nginx -t; then
    echo "✗ Nginx設定エラー"
    exit 1
fi

echo "Nginxを再起動中..."
sudo systemctl restart nginx

echo "✓ Nginx設定完了"
echo ""

echo "Step 8: 環境ファイルの作成"
if [ ! -f "$BACKEND_INSTALL_DIR/.env" ]; then
    cat > /tmp/.env.tmp <<'EOF'
spring.application.name=free-market-backend
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=jdbc:postgresql://localhost:5432/free_market123
spring.datasource.username=student
spring.datasource.password=himitu
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=noreply@furima.local
spring.mail.password=password

logging.level.root=INFO
logging.level.com.example.furima=DEBUG

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
EOF
    sudo mv /tmp/.env.tmp "$BACKEND_INSTALL_DIR/.env"
    sudo chown furima:furima "$BACKEND_INSTALL_DIR/.env"
    echo "✓ 環境ファイルを作成しました"
    echo "  注意: $BACKEND_INSTALL_DIR/.env を編集して、メール設定を更新してください"
else
    echo "✓ 環境ファイルはすでに存在します"
fi
echo ""

echo "Step 9: パーミッション設定"
sudo useradd -r -s /bin/false furima 2>/dev/null || true
sudo chown -R furima:furima "$INSTALL_DIR"
sudo chown -R furima:nginx /var/www/furima
echo "✓ パーミッション設定完了"
echo ""

echo "Step 10: サービスの開始"
echo "Spring Bootサービスを開始中..."
sudo systemctl start furima-backend

sleep 3

if sudo systemctl is-active --quiet furima-backend; then
    echo "✓ Spring Bootサービスが起動しました"
else
    echo "✗ Spring Bootサービスの起動に失敗しました"
    echo "ログを確認: sudo journalctl -u furima-backend -n 50"
    exit 1
fi
echo ""

echo "=== デプロイ完了 ==="
echo ""
echo "アプリケーションは以下のURLでアクセスできます:"
echo "  http://localhost"
echo ""
echo "ステータス確認:"
echo "  sudo systemctl status furima-backend"
echo ""
echo "ログ確認:"
echo "  sudo journalctl -u furima-backend -f"
echo ""
echo "再起動:"
echo "  sudo systemctl restart furima-backend"
echo ""
