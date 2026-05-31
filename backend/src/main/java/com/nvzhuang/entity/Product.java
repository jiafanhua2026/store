package com.nvzhuang.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(length = 50)
    private String category;
    
    @Column(length = 500)
    private String image;
    
    @Column(length = 1000)
    private String detail;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;
    
    @Column(length = 50)
    private String style;
    
    @Column(length = 50)
    private String fabric;
    
    @Column(length = 50)
    private String season;
    
    @Column(nullable = false)
    private Integer status = 1;
    
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