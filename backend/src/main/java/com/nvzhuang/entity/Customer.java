package com.nvzhuang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 20)
    private String phone;
    
    @Column(length = 100)
    private String wechat;
    
    @Column(length = 500)
    private String address;
    
    @Column(length = 20)
    private String level = "普通";
    
    @Column(name = "total_order")
    private Integer totalOrder = 0;
    
    @Column(name = "total_amount", precision = 12, scale = 2)
    private java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
    
    @Column(name = "last_purchase_time")
    private LocalDateTime lastPurchaseTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}