package com.nvzhuang.service.impl;

import com.nvzhuang.entity.Product;
import com.nvzhuang.entity.ProductSku;
import com.nvzhuang.repository.ProductRepository;
import com.nvzhuang.repository.ProductSkuRepository;
import com.nvzhuang.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductSkuRepository productSkuRepository;
    
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @Override
    public List<Product> getProductsByStatus(Integer status) {
        return productRepository.findByStatus(status);
    }
    
    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Override
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isPresent()) {
            Product p = existing.get();
            p.setName(product.getName());
            p.setCategory(product.getCategory());
            p.setImage(product.getImage());
            p.setDetail(product.getDetail());
            p.setCostPrice(product.getCostPrice());
            p.setSalePrice(product.getSalePrice());
            p.setStyle(product.getStyle());
            p.setFabric(product.getFabric());
            p.setSeason(product.getSeason());
            p.setStatus(product.getStatus());
            return productRepository.save(p);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void updateProductStatus(Long id, Integer status) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isPresent()) {
            Product p = existing.get();
            p.setStatus(status);
            productRepository.save(p);
        }
    }
    
    @Override
    public List<ProductSku> getSkusByProductId(Long productId) {
        return productSkuRepository.findByProductId(productId);
    }
    
    @Override
    public ProductSku getSkuById(Long id) {
        return productSkuRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public ProductSku createSku(ProductSku sku) {
        return productSkuRepository.save(sku);
    }
    
    @Override
    @Transactional
    public ProductSku updateSku(Long id, ProductSku sku) {
        Optional<ProductSku> existing = productSkuRepository.findById(id);
        if (existing.isPresent()) {
            ProductSku s = existing.get();
            s.setSize(sku.getSize());
            s.setColor(sku.getColor());
            s.setStock(sku.getStock());
            return productSkuRepository.save(s);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteSku(Long id) {
        productSkuRepository.deleteById(id);
    }
}