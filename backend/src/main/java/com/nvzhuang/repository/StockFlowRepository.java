package com.nvzhuang.repository;

import com.nvzhuang.entity.StockFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockFlowRepository extends JpaRepository<StockFlow, Long> {
    List<StockFlow> findByProductId(Long productId);
    List<StockFlow> findBySkuId(Long skuId);
    List<StockFlow> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<StockFlow> findByType(String type);
}