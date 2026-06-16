package com.example.furima.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.furima.dto.ItemSummaryDto;
import com.example.furima.entity.Categories;
import com.example.furima.entity.Users;
import com.example.furima.form.ItemSearchForm;
import com.example.furima.service.ItemListService;
import com.example.furima.support.LoginUserProvider;

import jakarta.servlet.http.HttpSession;

/**
 * item-101（トップ／物品一覧画面）の Controller。
 * <p>
 * 物品一覧の表示と、キーワード／カテゴリ／状態による絞り込みを担当する。<br>
 * <b>本 Controller は item-101 画面専用。</b>他画面（ログイン・出品・マイページ等）の
 * 処理は含めず、画面内のリンクはダミーURLとしている（結合時に各担当のパスへ差し替え）。
 * </p>
 *
 * @author item-101 担当
 */
@Controller
public class Item101Controller {

    private final ItemListService itemListService;
    private final LoginUserProvider loginUserProvider;

    /**
     * コンストラクタインジェクション。
     *
     * @param itemListService   物品一覧サービス（item-101 専用）
     * @param loginUserProvider ログイン中ユーザ取得ヘルパー（現在はダミー）
     */
    public Item101Controller(ItemListService itemListService,
                             LoginUserProvider loginUserProvider) {
        this.itemListService = itemListService;
        this.loginUserProvider = loginUserProvider;
    }

    /**
     * トップ／物品一覧画面（item-101）を表示する。
     * <p>
     * 検索フォーム未指定（初回アクセス）の場合は既定条件（状態＝出品中・新着順）で一覧表示する。
     * </p>
     *
     * @param form    検索・絞り込み条件（クエリパラメータからバインド）
     * @param session HTTPセッション（ログインユーザ取得に使用）
     * @param model   ビューへ渡すモデル
     * @return ビュー名（templates/item101.html）
     */
    @GetMapping({"/", "/item-101"})
    public String showItemList(@ModelAttribute("searchForm") ItemSearchForm form,
                               HttpSession session,
                               Model model) {

        // TODO: 並行開発中のログイン機能と結合する（現在はダミーユーザを取得）。
        Users loginUser = loginUserProvider.getCurrentUser(session);

        List<ItemSummaryDto> items = itemListService.findItems(form);
        List<Categories> categories = itemListService.findAllCategories();

        model.addAttribute("loginUser", loginUser);
        model.addAttribute("items", items);
        model.addAttribute("categories", categories);
        model.addAttribute("resultCount", items.size());
        // searchForm は @ModelAttribute により自動でモデルに追加される

        return "item101";
    }
}
