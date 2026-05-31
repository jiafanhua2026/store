package com.nvzhuang.service;

import com.nvzhuang.entity.Order;
import com.nvzhuang.entity.OrderItem;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(Long customerId, Long salesId, List<OrderItem> items);
    Order getOrderById(Long id);
    Order getOrderByOrderNo(String orderNo);
    List<Order> getOrdersByCustomerId(Long customerId);
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByDateRange(String startDate, String endDate);
    Order updateOrderStatus(Long id, String status);
    Order updateOrderPrice(Long id, java.math.BigDecimal amount);
    void deleteOrder(Long id);
    Map<String, Object> getTodayStats();
}