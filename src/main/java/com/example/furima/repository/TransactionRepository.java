package com.example.furima.repository;

import com.example.furima.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    Optional<Transaction> findByItemId(Integer itemId);
    List<Transaction> findBySellerId(Integer sellerId);
    List<Transaction> findByBuyerId(Integer buyerId);

    @Query("SELECT t FROM Transaction t WHERE t.transferCode = :transferCode")
    Optional<Transaction> findByTransferCode(@Param("transferCode") String transferCode);
}
