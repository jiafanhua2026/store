package com.nvzhuang.repository;

import com.nvzhuang.entity.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, Long> {
    List<ProductSku> findByProductId(Long productId);
    ProductSku findByProductIdAndSizeAndColor(Long productId, String size, String color);
    List<ProductSku> findByStockLessThan(Integer stock);
}