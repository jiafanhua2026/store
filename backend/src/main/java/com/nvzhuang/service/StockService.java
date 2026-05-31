package com.nvzhuang.service;

import com.nvzhuang.entity.StockFlow;

import java.math.BigDecimal;
import java.util.List;

public interface StockService {
    boolean deductStock(Long skuId, Integer quantity);
    boolean addStock(Long skuId, Integer quantity, BigDecimal costPrice, String remark);
    Integer getStockBySkuId(Long skuId);
    List<StockFlow> getStockFlows(Long productId, Long skuId);
    List<StockFlow> getStockFlowsByDateRange(String startDate, String endDate);
    List<StockFlow> getLowStockItems(Integer threshold);
}