package com.example.furima.repository;

import com.example.furima.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {
    List<Item> findByUserId(Integer userId);
    List<Item> findByCategoryId(Integer categoryId);
    List<Item> findByStatus(Integer status);

    @Query("SELECT i FROM Item i WHERE " +
           "(LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND i.status IN :statusList")
    List<Item> findByKeywordAndStatusList(@Param("keyword") String keyword, @Param("statusList") List<Integer> statusList);

    @Query("SELECT i FROM Item i WHERE i.deadline < :now AND i.status = 1")
    List<Item> findExpiredItems(@Param("now") LocalDateTime now);
}
