package com.nvzhuang.service.impl;

import com.nvzhuang.entity.*;
import com.nvzhuang.repository.*;
import com.nvzhuang.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductSkuRepository productSkuRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ViewRecordRepository viewRecordRepository;
    
    private static final String VIEWS_KEY = "nvzhuang:views";
    private static final String TODAY_VIEWS_KEY = "nvzhuang:views:today";
    
    @Override
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalProducts", productRepository.count());
        stats.put("totalCustomers", customerRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("realtimeViews", getRealtimeViews());
        
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        
        Long todayOrders = orderRepository.countByDateRange(todayStart, todayEnd);
        BigDecimal todaySales = orderRepository.sumAmountByDateRange(todayStart, todayEnd);
        
        stats.put("todayOrders", todayOrders);
        stats.put("todaySales", todaySales != null ? todaySales : BigDecimal.ZERO);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getSalesStats(String period) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime start, end;
        
        switch (period) {
            case "week":
                start = LocalDate.now().minusWeeks(1).atStartOfDay();
                end = LocalDate.now().atTime(LocalTime.MAX);
                break;
            case "month":
                start = LocalDate.now().minusMonths(1).atStartOfDay();
                end = LocalDate.now().atTime(LocalTime.MAX);
                break;
            case "year":
                start = LocalDate.now().minusYears(1).atStartOfDay();
                end = LocalDate.now().atTime(LocalTime.MAX);
                break;
            default: // today
                start = LocalDate.now().atStartOfDay();
                end = LocalDate.now().atTime(LocalTime.MAX);
        }
        
        Long orderCount = orderRepository.countByDateRange(start, end);
        BigDecimal totalAmount = orderRepository.sumAmountByDateRange(start, end);
        
        stats.put("orderCount", orderCount);
        stats.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        stats.put("period", period);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getStockStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<ProductSku> skus = productSkuRepository.findAll();
        int totalStock = skus.stream().mapToInt(ProductSku::getStock).sum();
        
        long lowStockCount = skus.stream().filter(s -> s.getStock() < 10).count();
        
        Map<String, Long> categoryStock = new HashMap<>();
        Map<Long, Product> productMap = productRepository.findAll().stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        
        skus.forEach(sku -> {
            Product product = productMap.get(sku.getProductId());
            if (product != null) {
                categoryStock.merge(product.getCategory(), (long) sku.getStock(), Long::sum);
            }
        });
        
        stats.put("totalStock", totalStock);
        stats.put("lowStockCount", lowStockCount);
        stats.put("categoryStock", categoryStock);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getCustomerStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Customer> customers = customerRepository.findAll();
        long vipCount = customers.stream().filter(c -> "VIP".equals(c.getLevel())).count();
        long normalCount = customers.stream().filter(c -> "普通".equals(c.getLevel())).count();
        
        LocalDateTime churnThreshold = LocalDateTime.now().minusMonths(3);
        long churnWarningCount = customers.stream()
                .filter(c -> c.getLastPurchaseTime() != null && c.getLastPurchaseTime().isBefore(churnThreshold))
                .count();
        
        BigDecimal avgOrderValue = customers.stream()
                .filter(c -> c.getTotalOrder() > 0)
                .map(c -> c.getTotalAmount().divide(BigDecimal.valueOf(c.getTotalOrder()), 2, BigDecimal.ROUND_HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(Math.max(1, customers.size())), 2, BigDecimal.ROUND_HALF_UP);
        
        stats.put("totalCustomers", customers.size());
        stats.put("vipCount", vipCount);
        stats.put("normalCount", normalCount);
        stats.put("churnWarningCount", churnWarningCount);
        stats.put("avgOrderValue", avgOrderValue);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getTopProducts(Integer limit) {
        Map<String, Object> stats = new HashMap<>();
        
        return stats;
    }
    
    @Override
    public Long getRealtimeViews() {
        String todayViews = redisTemplate.opsForValue().get(TODAY_VIEWS_KEY);
        return todayViews != null ? Long.parseLong(todayViews) : 0;
    }
    
    @Override
    public void incrementViews() {
        redisTemplate.opsForValue().increment(VIEWS_KEY);
        redisTemplate.opsForValue().increment(TODAY_VIEWS_KEY);
    }
}