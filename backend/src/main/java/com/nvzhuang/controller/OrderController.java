package com.nvzhuang.controller;

import com.nvzhuang.entity.Order;
import com.nvzhuang.entity.OrderItem;
import com.nvzhuang.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@RequestParam(required = false) Long customerId,
                                                 @RequestParam(required = false) String status,
                                                 @RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate) {
        List<Order> orders;
        if (customerId != null) {
            orders = orderService.getOrdersByCustomerId(customerId);
        } else if (status != null) {
            orders = orderService.getOrdersByStatus(status);
        } else if (startDate != null && endDate != null) {
            orders = orderService.getOrdersByDateRange(startDate, endDate);
        } else {
            orders = orderService.getOrdersByStatus("待付款");
        }
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }
    
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
        try {
            Long customerId = body.get("customerId") != null ? ((Number) body.get("customerId")).longValue() : null;
            Long salesId = body.get("salesId") != null ? ((Number) body.get("salesId")).longValue() : null;
            List<OrderItem> items = (List<OrderItem>) body.get("items");
            
            Order order = orderService.createOrder(customerId, salesId, items);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Order updated = orderService.updateOrderStatus(id, body.get("status"));
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/{id}/price")
    public ResponseEntity<Order> updateOrderPrice(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        java.math.BigDecimal amount = new java.math.BigDecimal(body.get("amount").toString());
        Order updated = orderService.updateOrderPrice(id, amount);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/today-stats")
    public ResponseEntity<Map<String, Object>> getTodayStats() {
        return ResponseEntity.ok(orderService.getTodayStats());
    }
}