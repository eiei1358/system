package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ItemImages;

/**
 * 物品画像（item_images）リポジトリ。
 * <p>
 * item-101 の一覧ではサムネイル（各物品の先頭画像）を表示するため、
 * 物品IDをキーに画像を取得する。snake_case プロパティのため明示的 JPQL を使用する。
 * </p>
 *
 * @author item-101 担当
 */
@Repository
public interface ItemImagesRepository extends JpaRepository<ItemImages, Integer> {

    /**
     * 指定した物品IDに紐づく画像を画像ID昇順で取得する。
     *
     * @param item_id 物品ID。null 不可
     * @return 画像リスト（先頭がサムネイル候補。0件の場合は空リスト）
     */
    @Query("SELECT img FROM ItemImages img WHERE img.item_id = :item_id ORDER BY img.image_id ASC")
    List<ItemImages> findByItemId(@Param("item_id") Integer item_id);
}
