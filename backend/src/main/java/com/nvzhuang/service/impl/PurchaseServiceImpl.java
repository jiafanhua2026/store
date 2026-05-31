package com.nvzhuang.service.impl;

import com.nvzhuang.entity.PurchaseItem;
import com.nvzhuang.entity.PurchaseOrder;
import com.nvzhuang.entity.Product;
import com.nvzhuang.entity.ProductSku;
import com.nvzhuang.repository.PurchaseItemRepository;
import com.nvzhuang.repository.PurchaseOrderRepository;
import com.nvzhuang.repository.ProductRepository;
import com.nvzhuang.repository.ProductSkuRepository;
import com.nvzhuang.service.PurchaseService;
import com.nvzhuang.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    private PurchaseItemRepository purchaseItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductSkuRepository productSkuRepository;
    
    @Autowired
    private StockService stockService;
    
    @Override
    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder order, List<PurchaseItem> items) {
        BigDecimal totalAmount = items.stream()
                .map(PurchaseItem::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        order.setPurchaseNo("PO" + System.currentTimeMillis());
        
        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);
        
        for (PurchaseItem item : items) {
            item.setPurchaseId(savedOrder.getId());
            purchaseItemRepository.save(item);
        }
        
        return savedOrder;
    }
    
    @Override
    public PurchaseOrder getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id).orElse(null);
    }
    
    @Override
    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        return purchaseOrderRepository.findByStatus(status);
    }
    
    @Override
    @Transactional
    public PurchaseOrder updatePurchaseStatus(Long id, String status) {
        Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(id);
        if (orderOpt.isPresent()) {
            PurchaseOrder order = orderOpt.get();
            order.setStatus(status);
            return purchaseOrderRepository.save(order);
        }
        return null;
    }
    
    @Override
    @Transactional
    public boolean confirmPurchase(Long id) {
        Optional<PurchaseOrder> orderOpt = purchaseOrderRepository.findById(id);
        if (orderOpt.isPresent()) {
            PurchaseOrder order = orderOpt.get();
            
            List<PurchaseItem> items = purchaseItemRepository.findByPurchaseId(id);
            for (PurchaseItem item : items) {
                ProductSku sku = productSkuRepository.findByProductIdAndSizeAndColor(
                        item.getProductId(), item.getSize(), item.getColor());
                
                if (sku == null) {
                    Product product = productRepository.findById(item.getProductId()).orElse(null);
                    if (product != null) {
                        sku = new ProductSku();
                        sku.setProductId(item.getProductId());
                        sku.setSize(item.getSize());
                        sku.setColor(item.getColor());
                        sku.setStock(0);
                        sku = productSkuRepository.save(sku);
                        item.setSkuId(sku.getId());
                    }
                }
                
                if (sku != null) {
                    stockService.addStock(sku.getId(), item.getQuantity(), item.getUnitCost(), 
                            "采购入库: " + order.getPurchaseNo());
                    item.setStatus(1);
                    purchaseItemRepository.save(item);
                }
            }
            
            order.setStatus("已入库");
            purchaseOrderRepository.save(order);
            return true;
        }
        return false;
    }
    
    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }
}