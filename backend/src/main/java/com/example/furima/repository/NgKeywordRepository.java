package com.example.furima.repository;

import com.example.furima.entity.NgKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NgKeywordRepository extends JpaRepository<NgKeyword, Integer> {
    @Query("SELECT nk FROM NgKeyword nk WHERE nk.status = 1 AND nk.type IN :types")
    List<NgKeyword> findActiveKeywordsByTypes(@Param("types") List<Integer> types);

    List<NgKeyword> findByStatus(Integer status);
}
