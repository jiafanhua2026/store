package com.nvzhuang.service;

import com.nvzhuang.entity.PurchaseOrder;
import com.nvzhuang.entity.PurchaseItem;

import java.util.List;

public interface PurchaseService {
    PurchaseOrder createPurchaseOrder(PurchaseOrder order, List<PurchaseItem> items);
    PurchaseOrder getPurchaseOrderById(Long id);
    List<PurchaseOrder> getPurchaseOrdersByStatus(String status);
    PurchaseOrder updatePurchaseStatus(Long id, String status);
    boolean confirmPurchase(Long id);
    List<PurchaseOrder> getAllPurchaseOrders();
}