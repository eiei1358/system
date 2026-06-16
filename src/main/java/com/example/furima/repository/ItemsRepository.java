package com.example.furima.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.furima.entity.Items;

/**
 * 物品（items）リポジトリ。
 * <p>
 * item-101（物品一覧）の検索・一覧取得に必要なクエリを提供する。
 * エンティティ {@link Items} はプロパティ名が snake_case のため、
 * Spring Data の派生クエリ（メソッド名解決）では予期せぬ解釈を招く恐れがある。
 * そのため検索系は明示的な JPQL（{@link Query}）で定義している。
 * </p>
 *
 * @author item-101 担当
 */
@Repository
public interface ItemsRepository extends JpaRepository<Items, Integer> {

    /**
     * 出品状態を指定して物品を取得する（新しい順）。
     *
     * @param status 出品状態（例：「出品中」）。null 不可
     * @return 条件に一致する物品リスト（0件の場合は空リスト）
     */
    @Query("SELECT i FROM Items i WHERE i.status = :status ORDER BY i.created_at DESC")
    List<Items> findByStatusOrderByCreatedAtDesc(@Param("status") String status);

    /**
     * 物品一覧画面（item-101）の検索条件で物品を取得する。
     * <p>各条件は null を渡すと「指定なし（絞り込まない）」として扱う。</p>
     *
     * @param status      出品状態での絞り込み（null の場合は状態を問わない）
     * @param keyword      商品名・説明の部分一致キーワード（null/空の場合はキーワード絞り込みなし）
     * @param category_id  カテゴリIDでの絞り込み（null の場合は全カテゴリ）
     * @return 条件に一致する物品リスト（新しい順、0件の場合は空リスト）
     */
    @Query("SELECT i FROM Items i "
            + "WHERE (:status IS NULL OR i.status = :status) "
            + "AND (:keyword IS NULL OR i.name LIKE CONCAT('%', :keyword, '%') "
            + "     OR i.description LIKE CONCAT('%', :keyword, '%')) "
            + "AND (:category_id IS NULL OR i.category_id = :category_id) "
            + "ORDER BY i.created_at DESC")
    List<Items> search(@Param("status") String status,
                       @Param("keyword") String keyword,
                       @Param("category_id") Integer category_id);
}
