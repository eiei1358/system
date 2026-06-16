package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * スタンドアロン起動用のアプリケーションクラス。
 * <p>
 * UC-710（統計情報を閲覧する）の動作確認用。実プロジェクトに統合する場合は、
 * 既存の {@code @SpringBootApplication} クラスを使用し、本クラスは不要。
 * </p>
 */
@SpringBootApplication
public class MasterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MasterApplication.class, args);
    }
}
