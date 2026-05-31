package com.nvzhuang.controller;

import com.nvzhuang.entity.PurchaseItem;
import com.nvzhuang.entity.PurchaseOrder;
import com.nvzhuang.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase")
@CrossOrigin(origins = "*")
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;
    
    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getPurchaseOrders(@RequestParam(required = false) String status) {
        List<PurchaseOrder> orders;
        if (status != null) {
            orders = purchaseService.getPurchaseOrdersByStatus(status);
        } else {
            orders = purchaseService.getAllPurchaseOrders();
        }
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable Long id) {
        PurchaseOrder order = purchaseService.getPurchaseOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
    
    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(@RequestBody Map<String, Object> body) {
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier((String) body.get("supplier"));
        order.setRemark((String) body.getOrDefault("remark", ""));
        
        List<PurchaseItem> items = (List<PurchaseItem>) body.get("items");
        PurchaseOrder created = purchaseService.createPurchaseOrder(order, items);
        return ResponseEntity.ok(created);
    }
    
    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmPurchase(@PathVariable Long id) {
        boolean success = purchaseService.confirmPurchase(id);
        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "入库成功"));
        }
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "入库失败"));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrder> updatePurchaseStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        PurchaseOrder updated = purchaseService.updatePurchaseStatus(id, body.get("status"));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
}