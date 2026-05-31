package com.nvzhuang.service;

import java.util.Map;

public interface StatsService {
    Map<String, Object> getOverviewStats();
    Map<String, Object> getSalesStats(String period);
    Map<String, Object> getStockStats();
    Map<String, Object> getCustomerStats();
    Map<String, Object> getTopProducts(Integer limit);
    Long getRealtimeViews();
    void incrementViews();
}