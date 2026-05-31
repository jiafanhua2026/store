package com.nvzhuang.repository;

import com.nvzhuang.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(Integer status);
    List<Product> findByCategory(String category);
    List<Product> findByNameContaining(String name);
}