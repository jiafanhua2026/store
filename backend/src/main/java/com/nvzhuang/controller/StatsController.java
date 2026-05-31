package com.nvzhuang.controller;

import com.nvzhuang.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {
    
    @Autowired
    private StatsService statsService;
    
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverviewStats() {
        return ResponseEntity.ok(statsService.getOverviewStats());
    }
    
    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesStats(@RequestParam(defaultValue = "today") String period) {
        return ResponseEntity.ok(statsService.getSalesStats(period));
    }
    
    @GetMapping("/stock")
    public ResponseEntity<Map<String, Object>> getStockStats() {
        return ResponseEntity.ok(statsService.getStockStats());
    }
    
    @GetMapping("/customer")
    public ResponseEntity<Map<String, Object>> getCustomerStats() {
        return ResponseEntity.ok(statsService.getCustomerStats());
    }
    
    @GetMapping("/top-products")
    public ResponseEntity<Map<String, Object>> getTopProducts(@RequestParam(defaultValue = "10") Integer limit) {
        return ResponseEntity.ok(statsService.getTopProducts(limit));
    }
    
    @GetMapping("/views")
    public ResponseEntity<Long> getRealtimeViews() {
        return ResponseEntity.ok(statsService.getRealtimeViews());
    }
    
    @PostMapping("/views")
    public ResponseEntity<Long> incrementViews() {
        statsService.incrementViews();
        return ResponseEntity.ok(statsService.getRealtimeViews());
    }
}