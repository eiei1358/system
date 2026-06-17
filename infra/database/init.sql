\c postgres

drop database if exists free_market123;
drop role if exists student;
create role student password 'himitu' login;
create database free_market123 owner student;

\c free_market123

--------------------------------------------------
-- テーブル削除
--------------------------------------------------
DROP TABLE IF EXISTS ng_keywords cascade;
DROP TABLE IF EXISTS login_history cascade;
DROP TABLE IF EXISTS item_images cascade;
DROP TABLE IF EXISTS categories cascade;
DROP TABLE IF EXISTS messages cascade;
DROP TABLE IF EXISTS transactions cascade;
DROP TABLE IF EXISTS applications cascade;
DROP TABLE IF EXISTS items cascade;
DROP TABLE IF EXISTS users cascade;

--------------------------------------------------
-- users（社員）
--------------------------------------------------
CREATE TABLE users
(
  user_id INTEGER PRIMARY KEY,
  name VARCHAR NOT NULL,
  email VARCHAR NOT NULL UNIQUE,
  password CHAR(64) NOT NULL,
  role INTEGER NOT NULL,
  department VARCHAR,
  status INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL,
  icon_url VARCHAR,
  login_at TIMESTAMP NOT NULL,
  temporary_password BOOLEAN NOT NULL,
  last_password_change TIMESTAMP NOT NULL,
  login_fail_count INTEGER DEFAULT 0
);

--------------------------------------------------
-- categories（カテゴリ）
--------------------------------------------------
CREATE TABLE categories
(
  category_id SERIAL PRIMARY KEY,
  name VARCHAR NOT NULL
);

--------------------------------------------------
-- items（物品）
--------------------------------------------------
CREATE TABLE items
(
  item_id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  name VARCHAR NOT NULL,
  description VARCHAR,
  condition INTEGER NOT NULL,
  price NUMERIC(8,2),
  type INTEGER NOT NULL,
  status INTEGER NOT NULL,
  category_id INTEGER NOT NULL,
  deadline TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL,
  place INTEGER NOT NULL,
  negotiable BOOLEAN DEFAULT FALSE,
  reported BOOLEAN DEFAULT FALSE
);

--------------------------------------------------
-- item_images（画像）
--------------------------------------------------
CREATE TABLE item_images
(
  image_id SERIAL PRIMARY KEY,
  item_id INTEGER NOT NULL,
  image_url VARCHAR NOT NULL
);

--------------------------------------------------
-- applications（応募）
--------------------------------------------------
CREATE TABLE applications
(
  application_id SERIAL PRIMARY KEY,
  item_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  bid_price NUMERIC(8,2),
  status INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL
);

--------------------------------------------------
-- transactions（取引）
--------------------------------------------------
CREATE TABLE transactions
(
  transaction_id SERIAL PRIMARY KEY,
  item_id INTEGER NOT NULL,
  seller_id INTEGER NOT NULL,
  buyer_id INTEGER NOT NULL,
  status INTEGER NOT NULL,
  completed_at TIMESTAMP,
  seller_completed BOOLEAN DEFAULT FALSE,
  buyer_completed BOOLEAN DEFAULT FALSE,
  transfer_code VARCHAR UNIQUE
);

--------------------------------------------------
-- messages（メッセージ）
--------------------------------------------------
CREATE TABLE messages
(
  message_id SERIAL PRIMARY KEY,
  sender_id INTEGER NOT NULL,
  receiver_id INTEGER NOT NULL,
  item_id INTEGER,
  content VARCHAR NOT NULL,
  created_at TIMESTAMP NOT NULL,
  is_admin_message BOOLEAN DEFAULT FALSE,
  status INTEGER DEFAULT 1
);

--------------------------------------------------
-- ng_keywords（NGワード）
--------------------------------------------------
CREATE TABLE ng_keywords
(
  keyword_id SERIAL PRIMARY KEY,
  keyword VARCHAR NOT NULL,
  type INTEGER NOT NULL,
  status INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL
);

--------------------------------------------------
-- login_history（ログイン履歴）
--------------------------------------------------
CREATE TABLE login_history
(
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  login_at TIMESTAMP NOT NULL
);

--------------------------------------------------
-- 外部キー制約
--------------------------------------------------
ALTER TABLE items
  ADD FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE items
  ADD FOREIGN KEY (category_id) REFERENCES categories(category_id);

ALTER TABLE item_images
  ADD FOREIGN KEY (item_id) REFERENCES items(item_id);

ALTER TABLE applications
  ADD FOREIGN KEY (item_id) REFERENCES items(item_id);

ALTER TABLE applications
  ADD FOREIGN KEY (user_id) REFERENCES users(user_id);

ALTER TABLE transactions
  ADD FOREIGN KEY (item_id) REFERENCES items(item_id);

ALTER TABLE transactions
  ADD FOREIGN KEY (seller_id) REFERENCES users(user_id);

ALTER TABLE transactions
  ADD FOREIGN KEY (buyer_id) REFERENCES users(user_id);

ALTER TABLE messages
  ADD FOREIGN KEY (sender_id) REFERENCES users(user_id);

ALTER TABLE messages
  ADD FOREIGN KEY (receiver_id) REFERENCES users(user_id);

ALTER TABLE messages
  ADD FOREIGN KEY (item_id) REFERENCES items(item_id);

ALTER TABLE login_history
  ADD FOREIGN KEY (user_id) REFERENCES users(user_id);

--------------------------------------------------
-- 所有者設定
--------------------------------------------------
ALTER TABLE users OWNER TO student;
ALTER TABLE items OWNER TO student;
ALTER TABLE applications OWNER TO student;
ALTER TABLE transactions OWNER TO student;
ALTER TABLE messages OWNER TO student;
ALTER TABLE categories OWNER TO student;
ALTER TABLE item_images OWNER TO student;
ALTER TABLE ng_keywords OWNER TO student;
ALTER TABLE login_history OWNER TO student;
