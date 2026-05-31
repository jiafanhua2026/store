package com.nvzhuang.service;

import com.nvzhuang.entity.Product;
import com.nvzhuang.entity.ProductSku;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    List<Product> getProductsByStatus(Integer status);
    List<Product> getProductsByCategory(String category);
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    void updateProductStatus(Long id, Integer status);
    
    List<ProductSku> getSkusByProductId(Long productId);
    ProductSku getSkuById(Long id);
    ProductSku createSku(ProductSku sku);
    ProductSku updateSku(Long id, ProductSku sku);
    void deleteSku(Long id);
}