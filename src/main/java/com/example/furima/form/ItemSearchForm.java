package com.example.furima.form;

import lombok.Data;

/**
 * item-101（物品一覧画面）の検索・絞り込みフォーム。
 * <p>
 * 画面上部の「キーワード検索」および「すべて／カテゴリ／状態／期限」タブの入力を受け取る。
 * 本フォームは item-101 画面専用であり、他画面と共有しない。
 * </p>
 *
 * @author item-101 担当
 */
@Data
public class ItemSearchForm {

    /** フリーワード（商品名・説明の部分一致）。未入力可 */
    private String keyword;

    /** 絞り込みカテゴリID（categories.category_id）。未選択（すべて）の場合は null */
    private Integer category_id;

    /**
     * 出品状態の絞り込み。
     * 既定は「出品中」（公開中の物品のみ表示）。
     * 「すべて」を表す場合は空文字を渡すこと（Controller 側で null に正規化する）。
     */
    private String status = "出品中";

    /**
     * 並び順。
     * <ul>
     *   <li>{@code "newest"} … 登録日時の新しい順（既定）</li>
     *   <li>{@code "deadline"} … 登録期限の近い順</li>
     * </ul>
     */
    private String sort = "newest";
}
