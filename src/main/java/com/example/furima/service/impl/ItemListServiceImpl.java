package com.example.furima.service.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.furima.dto.ItemSummaryDto;
import com.example.furima.entity.Categories;
import com.example.furima.entity.ItemImages;
import com.example.furima.entity.Items;
import com.example.furima.form.ItemSearchForm;
import com.example.furima.repository.CategoriesRepository;
import com.example.furima.repository.ItemImagesRepository;
import com.example.furima.repository.ItemsRepository;
import com.example.furima.service.ItemListService;

/**
 * {@link ItemListService} の実装。
 * <p>
 * 物品・カテゴリ・画像の各リポジトリから取得した情報を突き合わせ、
 * item-101（物品一覧画面）の表示用 DTO を組み立てる。
 * </p>
 *
 * @author item-101 担当
 */
@Service
public class ItemListServiceImpl implements ItemListService {

    /** 形式（type）の定数：無償 */
    private static final String TYPE_FREE = "無償";
    /** 形式（type）の定数：オークション方式 */
    private static final String TYPE_AUCTION = "オークション";

    private final ItemsRepository itemsRepository;
    private final CategoriesRepository categoriesRepository;
    private final ItemImagesRepository itemImagesRepository;

    /**
     * コンストラクタインジェクション。
     *
     * @param itemsRepository       物品リポジトリ
     * @param categoriesRepository  カテゴリリポジトリ
     * @param itemImagesRepository  物品画像リポジトリ
     */
    public ItemListServiceImpl(ItemsRepository itemsRepository,
                               CategoriesRepository categoriesRepository,
                               ItemImagesRepository itemImagesRepository) {
        this.itemsRepository = itemsRepository;
        this.categoriesRepository = categoriesRepository;
        this.itemImagesRepository = itemImagesRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ItemSummaryDto> findItems(ItemSearchForm form) {
        // 「すべて」を表す空文字は null に正規化（リポジトリ側で「絞り込まない」と解釈される）
        String status = isBlank(form.getStatus()) ? null : form.getStatus();
        String keyword = isBlank(form.getKeyword()) ? null : form.getKeyword().trim();
        Integer categoryId = form.getCategory_id();

        List<Items> items = itemsRepository.search(status, keyword, categoryId);

        // カテゴリID → カテゴリ名 の辞書（N+1 回避のため一括取得）
        Map<Integer, String> categoryNameMap = categoriesRepository.findAll().stream()
                .collect(Collectors.toMap(Categories::getCategory_id, Categories::getName, (a, b) -> a));

        List<ItemSummaryDto> result = items.stream()
                .map(item -> toDto(item, categoryNameMap))
                .collect(Collectors.toList());

        // 並び順：期限の近い順が指定された場合のみ並べ替え（既定は検索クエリの新しい順）
        if ("deadline".equals(form.getSort())) {
            result.sort(Comparator.comparing(
                    ItemSummaryDto::getRemainingDays,
                    Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Categories> findAllCategories() {
        return categoriesRepository.findAll();
    }

    /**
     * 物品エンティティを画面表示用 DTO に変換する。
     *
     * @param item            物品エンティティ
     * @param categoryNameMap カテゴリID→名称の辞書
     * @return 表示用 DTO
     */
    private ItemSummaryDto toDto(Items item, Map<Integer, String> categoryNameMap) {
        // サムネイル：先頭画像を採用（無ければ null）
        List<ItemImages> images = itemImagesRepository.findByItemId(item.getItem_id());
        String thumbnailUrl = images.isEmpty() ? null : images.get(0).getImage_url();

        // 残り日数（期限まで）
        Long remainingDays = null;
        if (item.getDeadline() != null) {
            remainingDays = ChronoUnit.DAYS.between(LocalDate.now(), item.getDeadline());
        }

        String categoryName = categoryNameMap.getOrDefault(item.getCategory_id(), "");

        return ItemSummaryDto.builder()
                .item_id(item.getItem_id())
                .name(item.getName())
                .categoryName(categoryName)
                .condition(item.getCondition())
                .priceLabel(buildPriceLabel(item))
                .status(item.getStatus())
                .remainingDays(remainingDays)
                .expired(item.isExpired())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

    /**
     * 形式（type）と価格（price）から、一覧に表示する価格ラベルを生成する。
     * <p>
     * 仕様根拠：トップ画面（item-101）の一覧では「¥1,500／無償／オークション」のように
     * 形式に応じた表示を行う（画面設計図 2. トップ（物品一覧）画面）。
     * </p>
     *
     * @param item 物品エンティティ
     * @return 価格表示ラベル
     */
    private String buildPriceLabel(Items item) {
        if (TYPE_FREE.equals(item.getType())) {
            return "無償";
        }
        if (TYPE_AUCTION.equals(item.getType())) {
            return "オークション";
        }
        // 有償（その他）
        if (item.getPrice() == null) {
            return "応談可";
        }
        return String.format("¥%,d", item.getPrice().longValue());
    }

    /**
     * 文字列が null または空白のみかを判定する。
     *
     * @param s 判定対象
     * @return null または空白のみの場合 true
     */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
