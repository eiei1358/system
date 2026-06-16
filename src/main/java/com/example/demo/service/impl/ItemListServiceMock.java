package com.example.demo.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ItemSummaryDto;
import com.example.demo.entity.Categories;
import com.example.demo.form.ItemSearchForm;
import com.example.demo.service.ItemListService;

/**
 * item-101（物品一覧画面）の <b>モック（ダミーデータ）</b> サービス実装。
 * <p>
 * <b>【一時対応】データベース（テーブル）がまだ作成されていないため、DBにアクセスせず
 * メモリ上のダミーデータで画面表示を確認できるようにするための実装。</b><br>
 * {@link org.springframework.context.annotation.Primary @Primary} を付与しているため、
 * DBアクセス版（{@link ItemListServiceImpl}）より優先して使用される。
 * </p>
 *
 * <pre>
 * // TODO: DB（items / categories / item_images）構築後に本クラスを削除（または @Primary を外す）し、
 * //       DBアクセス版 ItemListServiceImpl を使用すること。
 * </pre>
 *
 * @author item-101 担当
 */
@Service
@Primary
public class ItemListServiceMock implements ItemListService {

    /** ダミーのカテゴリ一覧。 */
    private static final List<Categories> DUMMY_CATEGORIES = Arrays.asList(
            new Categories(1, "書籍"),
            new Categories(2, "IT機器"),
            new Categories(3, "文具"));

    /**
     * {@inheritDoc}
     * <p>ダミー物品を返す。検索条件はキーワードのみ簡易的に反映する（部分一致）。</p>
     */
    @Override
    public List<ItemSummaryDto> findItems(ItemSearchForm form) {
        List<ItemSummaryDto> all = new ArrayList<>();
        all.add(buildDummy(1, "商品A：技術書（中古）", "書籍", "中古", "¥1,500", "出品中", 5));
        all.add(buildDummy(2, "商品B：マウス（無償）", "IT機器", "新品同様", "無償", "出品中", 3));
        all.add(buildDummy(3, "商品C：ノートPC（応談可）", "IT機器", "中古", "応談可", "出品中", 10));
        all.add(buildDummy(4, "商品D：ボールペン詰め合わせ", "文具", "新品", "¥300", "出品中", 20));
        all.add(buildDummy(5, "商品E：参考書セット", "書籍", "中古", "¥800", "期限切れ", -2));

        // キーワード簡易フィルタ（指定があれば商品名で部分一致）
        String keyword = form != null ? form.getKeyword() : null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            return all.stream()
                    .filter(d -> d.getName() != null && d.getName().contains(kw))
                    .collect(Collectors.toList());
        }
        return all;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Categories> findAllCategories() {
        return DUMMY_CATEGORIES;
    }

    /**
     * 表示用ダミー DTO を生成する。
     *
     * @param id            物品ID
     * @param name          商品名
     * @param category      カテゴリ名
     * @param condition     商品の状態
     * @param priceLabel    価格表示ラベル
     * @param status        出品状態
     * @param remainingDays 残り日数（負数で期限切れ）
     * @return ダミーの {@link ItemSummaryDto}
     */
    private ItemSummaryDto buildDummy(int id, String name, String category, String condition,
                                      String priceLabel, String status, int remainingDays) {
        boolean expired = remainingDays < 0;
        // deadline は使用しないが、残り日数の整合のため算出値をそのまま保持
        long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusDays(remainingDays));
        return ItemSummaryDto.builder()
                .item_id(id)
                .name(name)
                .categoryName(category)
                .condition(condition)
                .priceLabel(priceLabel)
                .status(status)
                .remainingDays(days)
                .expired(expired)
                .thumbnailUrl(null) // 画像なし（NO IMAGE 表示）
                .build();
    }
}
