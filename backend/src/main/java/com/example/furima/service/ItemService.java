package com.example.furima.service;

import com.example.furima.dto.ItemDTO;
import com.example.furima.entity.*;
import com.example.furima.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final NgKeywordRepository ngKeywordRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public ItemDTO createItem(ItemDTO itemDTO) {
        validateItemContent(itemDTO.getName(), itemDTO.getDescription());

        User user = userRepository.findByUserId(itemDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        Category category = categoryRepository.findById(itemDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("カテゴリが見つかりません"));

        Item item = Item.builder()
                .user(user)
                .name(itemDTO.getName())
                .description(itemDTO.getDescription())
                .condition(itemDTO.getCondition())
                .price(itemDTO.getPrice())
                .type(itemDTO.getType())
                .status(1)
                .category(category)
                .deadline(itemDTO.getDeadline())
                .createdAt(LocalDateTime.now())
                .place(itemDTO.getPlace())
                .negotiable(itemDTO.getNegotiable() != null ? itemDTO.getNegotiable() : false)
                .reported(false)
                .build();

        Item savedItem = itemRepository.save(item);

        if (itemDTO.getImageUrls() != null && !itemDTO.getImageUrls().isEmpty()) {
            for (String imageUrl : itemDTO.getImageUrls()) {
                ItemImage image = ItemImage.builder()
                        .item(savedItem)
                        .imageUrl(imageUrl)
                        .build();
                itemImageRepository.save(image);
            }
        }

        return mapToDTO(savedItem);
    }

    @Transactional(readOnly = true)
    public ItemDTO getItemById(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品が見つかりません"));
        return mapToDTO(item);
    }

    @Transactional
    public ItemDTO updateItem(Integer itemId, ItemDTO itemDTO) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品が見つかりません"));

        if (!item.getUserId().equals(itemDTO.getUserId())) {
            throw new RuntimeException("権限がありません");
        }

        validateItemContent(itemDTO.getName(), itemDTO.getDescription());

        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setCondition(itemDTO.getCondition());
        item.setPrice(itemDTO.getPrice());
        item.setNegotiable(itemDTO.getNegotiable() != null ? itemDTO.getNegotiable() : false);
        item.setPlace(itemDTO.getPlace());

        Item updatedItem = itemRepository.save(item);
        return mapToDTO(updatedItem);
    }

    @Transactional
    public void extendDeadline(Integer itemId, LocalDateTime newDeadline) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品が見つかりません"));

        if (item.getStatus() != 1) {
            throw new RuntimeException("期限切れ状態の物品のみ延長可能です");
        }

        item.setDeadline(newDeadline);
        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> searchItems(Integer categoryId, String keyword, Integer condition,
                                    List<Integer> statusList, String sortBy, Boolean includeExpired) {
        Specification<Item> spec = Specification.where(null);

        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
            ));
        }

        if (condition != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("condition"), condition));
        }

        if (statusList != null && !statusList.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("status").in(statusList));
        }

        if (!includeExpired) {
            LocalDateTime now = LocalDateTime.now();
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.greaterThan(root.get("deadline"), now),
                    cb.equal(root.get("status"), 1)
            ));
        }

        List<Item> items = itemRepository.findAll(spec);

        if ("deadline".equals(sortBy)) {
            items.sort(Comparator.comparing(Item::getDeadline));
        } else if ("newest".equals(sortBy)) {
            items.sort(Comparator.comparing(Item::getCreatedAt).reversed());
        }

        return items.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void reportItem(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("物品が見つかりません"));
        item.setReported(true);
        itemRepository.save(item);
    }

    private void validateItemContent(String name, String description) {
        List<NgKeyword> ngKeywords = ngKeywordRepository.findActiveKeywordsByTypes(Arrays.asList(1, 3));

        for (NgKeyword keyword : ngKeywords) {
            if (name.contains(keyword.getKeyword()) || description.contains(keyword.getKeyword())) {
                throw new RuntimeException("入力内容に禁止ワードが含まれています: " + keyword.getKeyword());
            }
        }
    }

    private ItemDTO mapToDTO(Item item) {
        List<ItemImage> images = itemImageRepository.findByItemId(item.getItemId());
        List<String> imageUrls = images.stream()
                .map(ItemImage::getImageUrl)
                .collect(Collectors.toList());

        return ItemDTO.builder()
                .itemId(item.getItemId())
                .userId(item.getUserId())
                .userName(item.getUser().getName())
                .name(item.getName())
                .description(item.getDescription())
                .condition(item.getCondition())
                .price(item.getPrice())
                .type(item.getType())
                .status(item.getStatus())
                .categoryId(item.getCategoryId())
                .categoryName(item.getCategory().getName())
                .deadline(item.getDeadline())
                .createdAt(item.getCreatedAt())
                .place(item.getPlace())
                .negotiable(item.getNegotiable())
                .reported(item.getReported())
                .imageUrls(imageUrls)
                .build();
    }
}
