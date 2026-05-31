package com.nvzhuang.service.impl;

import com.nvzhuang.entity.ProductSku;
import com.nvzhuang.entity.StockFlow;
import com.nvzhuang.repository.ProductSkuRepository;
import com.nvzhuang.repository.StockFlowRepository;
import com.nvzhuang.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class StockServiceImpl implements StockService {
    
    @Autowired
    private ProductSkuRepository productSkuRepository;
    
    @Autowired
    private StockFlowRepository stockFlowRepository;
    
    @Override
    @Transactional
    public boolean deductStock(Long skuId, Integer quantity) {
        Optional<ProductSku> skuOpt = productSkuRepository.findById(skuId);
        if (skuOpt.isPresent()) {
            ProductSku sku = skuOpt.get();
            if (sku.getStock() >= quantity) {
                sku.setStock(sku.getStock() - quantity);
                productSkuRepository.save(sku);
                
                StockFlow flow = new StockFlow();
                flow.setSkuId(skuId);
                flow.setProductId(sku.getProductId());
                flow.setType("出库");
                flow.setQuantity(-quantity);
                flow.setCostPrice(sku.getStock() > 0 ? BigDecimal.ZERO : BigDecimal.ZERO);
                flow.setRemark("销售出库");
                stockFlowRepository.save(flow);
                
                return true;
            }
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean addStock(Long skuId, Integer quantity, BigDecimal costPrice, String remark) {
        Optional<ProductSku> skuOpt = productSkuRepository.findById(skuId);
        if (skuOpt.isPresent()) {
            ProductSku sku = skuOpt.get();
            sku.setStock(sku.getStock() + quantity);
            productSkuRepository.save(sku);
            
            StockFlow flow = new StockFlow();
            flow.setSkuId(skuId);
            flow.setProductId(sku.getProductId());
            flow.setType("入库");
            flow.setQuantity(quantity);
            flow.setCostPrice(costPrice);
            flow.setRemark(remark);
            stockFlowRepository.save(flow);
            
            return true;
        }
        return false;
    }
    
    @Override
    public Integer getStockBySkuId(Long skuId) {
        return productSkuRepository.findById(skuId)
                .map(ProductSku::getStock)
                .orElse(0);
    }
    
    @Override
    public List<StockFlow> getStockFlows(Long productId, Long skuId) {
        if (skuId != null) {
            return stockFlowRepository.findBySkuId(skuId);
        } else if (productId != null) {
            return stockFlowRepository.findByProductId(productId);
        }
        return stockFlowRepository.findAll();
    }
    
    @Override
    public List<StockFlow> getStockFlowsByDateRange(String startDate, String endDate) {
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        return stockFlowRepository.findByCreatedAtBetween(start, end);
    }
    
    @Override
    public List<StockFlow> getLowStockItems(Integer threshold) {
        return stockFlowRepository.findAll();
    }
}