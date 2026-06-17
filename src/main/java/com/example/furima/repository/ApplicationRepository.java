package com.example.furima.repository;

import com.example.furima.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByItemId(Integer itemId);
    List<Application> findByUserId(Integer userId);

    @Query("SELECT a FROM Application a WHERE a.item.id = :itemId ORDER BY a.bidPrice DESC")
    List<Application> findByItemIdOrderByBidPriceDesc(@Param("itemId") Integer itemId);

    @Query("SELECT a FROM Application a WHERE a.item.id = :itemId AND a.status = :status")
    Optional<Application> findByItemIdAndStatus(@Param("itemId") Integer itemId, @Param("status") Integer status);
}
