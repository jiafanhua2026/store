package com.nvzhuang.controller;

import com.nvzhuang.entity.Product;
import com.nvzhuang.entity.ProductSku;
import com.nvzhuang.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(@RequestParam(required = false) Integer status, 
                                                        @RequestParam(required = false) String category) {
        List<Product> products;
        if (status != null) {
            products = productService.getProductsByStatus(status);
        } else if (category != null) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/status")
    public ResponseEntity<Product> updateProductStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        productService.updateProductStatus(id, body.get("status"));
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @GetMapping("/{id}/skus")
    public ResponseEntity<List<ProductSku>> getProductSkus(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getSkusByProductId(id));
    }
    
    @PostMapping("/skus")
    public ResponseEntity<ProductSku> createSku(@RequestBody ProductSku sku) {
        return ResponseEntity.ok(productService.createSku(sku));
    }
    
    @PutMapping("/skus/{id}")
    public ResponseEntity<ProductSku> updateSku(@PathVariable Long id, @RequestBody ProductSku sku) {
        ProductSku updated = productService.updateSku(id, sku);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/skus/{id}")
    public ResponseEntity<Void> deleteSku(@PathVariable Long id) {
        productService.deleteSku(id);
        return ResponseEntity.ok().build();
    }
}