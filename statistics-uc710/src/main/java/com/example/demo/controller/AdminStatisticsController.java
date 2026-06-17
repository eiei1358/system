package com.example.demo.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.dto.StatisticsResult;
import com.example.demo.entity.Users;
import com.example.demo.form.StatisticsSearchForm;
import com.example.demo.service.StatisticsService;

/**
 * UC-710 統計情報閲覧（管理者用）の Controller。
 * <p>
 * 集計の観点（全体／カテゴリ別／時系列）と期間を受け取り、統計画面を表示する。
 * </p>
 *
 * @author UC-710 担当
 */
@Controller
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    /**
     * コンストラクタインジェクション。
     *
     * @param statisticsService 統計サービス
     */
    public AdminStatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * ログインユーザ（モック）。
     * <p>
     * <b>【並行開発中のモック認証】</b>ログイン機能は別メンバーが開発中のため、
     * 常に管理者（role=1）の仮ユーザをモデルへ供給する。<br>
     * 結合時は本メソッドを削除し、セッション（例：{@code session.getAttribute("loginUser")}）から
     * 取得した実ユーザを使用すること。
     * </p>
     *
     * @return 管理者の仮ユーザ
     */
    @ModelAttribute("loginUser")
    public Users mockLoginUser() {
        // TODO: 並行開発中のログイン機能と結合する。現在は管理者の仮データを返す。
        Users user = new Users();
        user.setUser_id(999);
        user.setName("管理者（モック）");
        user.setEmail("admin@example.com");
        user.setRole(1); // 1 = 管理者
        user.setStatus(0);
        user.setDepartment("管理部");
        user.setCreated_at(LocalDateTime.now());
        return user;
    }

    /**
     * 統計情報画面を表示する。
     *
     * @param form      集計条件（観点・期間・粒度。クエリパラメータからバインド）
     * @param loginUser モック認証で供給される管理者ユーザ
     * @param model     ビューへ渡すモデル
     * @return 統計画面（templates/statistics_view.html）／管理者以外はログインへリダイレクト
     */
    @GetMapping("/admin/statistics")
    public String statistics(@ModelAttribute("searchForm") StatisticsSearchForm form,
                             @ModelAttribute("loginUser") Users loginUser,
                             Model model) {

        // --- 管理者チェック ---
        if (loginUser == null || !loginUser.isAdmin()) {
            // TODO: 結合時は権限エラー画面の表示、またはログイン画面へのリダイレクトに差し替える。
            return "redirect:/login";
        }

        // 期間未指定（初回表示）の場合は直近6ヶ月を既定とし、画面の日付入力にも反映する
        if (form.getEndDate() == null) {
            form.setEndDate(java.time.LocalDate.now());
        }
        if (form.getStartDate() == null) {
            form.setStartDate(form.getEndDate().minusMonths(6));
        }

        StatisticsResult result = statisticsService.getStatistics(form);
        model.addAttribute("result", result);

        // --- ダッシュボード用：カテゴリ別・時系列の両方を並列リストでモデルへ ---
        java.util.List<String> categoryLabels = new java.util.ArrayList<>();
        java.util.List<Long> categoryListings = new java.util.ArrayList<>();
        java.util.List<Long> categoryDeals = new java.util.ArrayList<>();
        result.getCategoryStats().forEach(c -> {
            categoryLabels.add(c.getCategoryName());
            categoryListings.add(c.getListingCount());
            categoryDeals.add(c.getDealCount());
        });
        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("categoryListings", categoryListings);
        model.addAttribute("categoryDeals", categoryDeals);

        java.util.List<String> timeLabels = new java.util.ArrayList<>();
        java.util.List<Long> timeListings = new java.util.ArrayList<>();
        java.util.List<Long> timeDeals = new java.util.ArrayList<>();
        result.getTimeSeries().forEach(p -> {
            timeLabels.add(p.getLabel());
            timeListings.add(p.getListingCount());
            timeDeals.add(p.getDealCount());
        });
        model.addAttribute("timeLabels", timeLabels);
        model.addAttribute("timeListings", timeListings);
        model.addAttribute("timeDeals", timeDeals);

        return "statistics_view";
    }
}
