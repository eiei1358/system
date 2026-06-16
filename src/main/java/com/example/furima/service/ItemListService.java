package com.example.furima.service;

import java.util.List;

import com.example.furima.dto.ItemSummaryDto;
import com.example.furima.entity.Categories;
import com.example.furima.form.ItemSearchForm;

/**
 * item-101（物品一覧画面）のための一覧・検索サービス。
 * <p>
 * 物品の検索結果を画面表示用 DTO（{@link ItemSummaryDto}）に組み立てる責務を持つ。
 * Controller はこのインターフェースにのみ依存する（実装差し替え・テスト容易性のため）。
 * </p>
 * <p>
 * 【並行開発メモ】物品の「登録・更新・成約」など item-101 以外の操作は、
 * 別メンバーが開発する共有 ItemService の担当領域である。本インターフェースは
 * item-101 の「閲覧（一覧・検索）」のみを対象とし、責務境界を明確にしている。
 * </p>
 *
 * @author item-101 担当
 */
public interface ItemListService {

    /**
     * 検索条件に一致する物品の一覧（画面表示用）を取得する。
     *
     * @param form 検索・絞り込み条件（キーワード、カテゴリ、状態、並び順）。null 不可
     * @return 画面表示用の物品サマリのリスト（該当なしの場合は空リスト。null は返さない）
     */
    List<ItemSummaryDto> findItems(ItemSearchForm form);

    /**
     * 検索フィルタ用の全カテゴリを取得する。
     *
     * @return 全カテゴリのリスト（0件の場合は空リスト）
     */
    List<Categories> findAllCategories();
}
