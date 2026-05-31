package com.nvzhuang.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {
    
    @MessageMapping("/newOrder")
    @SendTo("/topic/orders")
    public Map<String, Object> handleNewOrder(Map<String, Object> order) {
        return order;
    }
    
    @MessageMapping("/updateStats")
    @SendTo("/topic/stats")
    public Map<String, Object> handleStatsUpdate(Map<String, Object> stats) {
        return stats;
    }
    
    @MessageMapping("/updateStock")
    @SendTo("/topic/stock")
    public Map<String, Object> handleStockUpdate(Map<String, Object> stock) {
        return stock;
    }
}