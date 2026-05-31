package com.nvzhuang.service.impl;

import com.nvzhuang.entity.*;
import com.nvzhuang.repository.*;
import com.nvzhuang.service.OrderService;
import com.nvzhuang.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductSkuRepository productSkuRepository;
    
    @Override
    @Transactional
    public Order createOrder(Long customerId, Long salesId, List<OrderItem> items) {
        for (OrderItem item : items) {
            if (!stockService.deductStock(item.getSkuId(), item.getQuantity())) {
                throw new RuntimeException("库存不足: " + item.getProductName());
            }
        }
        
        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Order order = new Order();
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setCustomerId(customerId);
        order.setSalesId(salesId);
        order.setAmount(totalAmount);
        order.setStatus("待付款");
        Order savedOrder = orderRepository.save(order);
        
        for (OrderItem item : items) {
            item.setOrderId(savedOrder.getId());
            orderItemRepository.save(item);
        }
        
        if (customerId != null) {
            updateCustomerStats(customerId, totalAmount);
        }
        
        return savedOrder;
    }
    
    private void updateCustomerStats(Long customerId, BigDecimal amount) {
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setTotalOrder(customer.getTotalOrder() + 1);
            customer.setTotalAmount(customer.getTotalAmount().add(amount));
            customer.setLastPurchaseTime(LocalDateTime.now());
            customerRepository.save(customer);
        }
    }
    
    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getOrderNo().equals(orderNo))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    
    @Override
    public List<Order> getOrdersByDateRange(String startDate, String endDate) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        return orderRepository.findByCreatedAtBetween(start, end);
    }
    
    @Override
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }
    
    @Override
    @Transactional
    public Order updateOrderPrice(Long id, BigDecimal amount) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setAmount(amount);
            return orderRepository.save(order);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteOrder(Long id) {
        orderItemRepository.findByOrderId(id).forEach(orderItemRepository::delete);
        orderRepository.deleteById(id);
    }
    
    @Override
    public Map<String, Object> getTodayStats() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        
        Long orderCount = orderRepository.countByDateRange(start, end);
        BigDecimal totalAmount = orderRepository.sumAmountByDateRange(start, end);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("orderCount", orderCount);
        stats.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        return stats;
    }
}