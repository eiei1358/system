package com.example.furima.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.furima.entity.Categories;

/**
 * カテゴリ（categories）リポジトリ。
 * <p>item-101 では検索フィルタ（カテゴリ選択）および一覧表示のカテゴリ名解決に利用する。</p>
 *
 * @author item-101 担当
 */
@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Integer> {
    // 全件取得（findAll）と主キー取得（findById）のみを使用するため追加メソッドは不要。
}
