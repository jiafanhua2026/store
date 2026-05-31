package com.nvzhuang.controller;

import com.nvzhuang.entity.StockFlow;
import com.nvzhuang.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "*")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @GetMapping("/{skuId}")
    public ResponseEntity<Integer> getStock(@PathVariable Long skuId) {
        return ResponseEntity.ok(stockService.getStockBySkuId(skuId));
    }
    
    @PostMapping("/deduct")
    public ResponseEntity<?> deductStock(@RequestBody Map<String, Object> body) {
        Long skuId = ((Number) body.get("skuId")).longValue();
        Integer quantity = (Integer) body.get("quantity");
        
        boolean success = stockService.deductStock(skuId, quantity);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "库存扣减成功"));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "库存不足"));
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addStock(@RequestBody Map<String, Object> body) {
        Long skuId = ((Number) body.get("skuId")).longValue();
        Integer quantity = (Integer) body.get("quantity");
        BigDecimal costPrice = new BigDecimal(body.get("costPrice").toString());
        String remark = (String) body.getOrDefault("remark", "入库");
        
        boolean success = stockService.addStock(skuId, quantity, costPrice, remark);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "库存增加成功"));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "操作失败"));
    }
    
    @GetMapping("/flows")
    public ResponseEntity<List<StockFlow>> getStockFlows(@RequestParam(required = false) Long productId,
                                                         @RequestParam(required = false) Long skuId,
                                                         @RequestParam(required = false) String startDate,
                                                         @RequestParam(required = false) String endDate) {
        List<StockFlow> flows;
        if (startDate != null && endDate != null) {
            flows = stockService.getStockFlowsByDateRange(startDate, endDate);
        } else {
            flows = stockService.getStockFlows(productId, skuId);
        }
        return ResponseEntity.ok(flows);
    }
}