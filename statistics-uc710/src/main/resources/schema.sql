-- =====================================================================
-- UC-710 統計：必要テーブルの作成（無ければ作成）
--   実スキーマ（free_market123）に整合：role/status/type/condition は INTEGER、
--   items.place / users.login_at 等を含む。既にテーブルがあれば作成をスキップする。
--   spring.sql.init.mode=always により起動時に実行される。
-- =====================================================================

CREATE TABLE IF NOT EXISTS users (
  user_id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  password CHAR(64) NOT NULL,
  role INTEGER NOT NULL,
  department VARCHAR,
  status INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL,
  icon_url VARCHAR,
  login_at TIMESTAMP NOT NULL,
  temporary_password BOOLEAN NOT NULL,
  last_password_change TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
  category_id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS items (
  item_id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  name VARCHAR NOT NULL,
  description VARCHAR,
  condition INTEGER NOT NULL,
  price NUMERIC(8,2),
  type INTEGER NOT NULL,
  status INTEGER NOT NULL,
  category_id INTEGER NOT NULL REFERENCES categories(category_id),
  deadline DATE NOT NULL,
  created_at TIMESTAMP NOT NULL,
  place INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
  transaction_id SERIAL PRIMARY KEY,
  item_id INTEGER NOT NULL REFERENCES items(item_id),
  seller_id INTEGER NOT NULL REFERENCES users(user_id),
  buyer_id INTEGER NOT NULL REFERENCES users(user_id),
  status INTEGER NOT NULL,
  completed_at TIMESTAMP
);
