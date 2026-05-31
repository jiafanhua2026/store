package com.nvzhuang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(name = "sku_id")
    private Long skuId;
    
    @Column(nullable = false, length = 200)
    private String productName;
    
    @Column(nullable = false, length = 50)
    private String size;
    
    @Column(nullable = false, length = 50)
    private String color;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCost;
    
    @Column(nullable = false)
    private Integer status = 0;
}