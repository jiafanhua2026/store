package com.nvzhuang.service;

import com.nvzhuang.entity.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Customer getCustomerById(Long id);
    Customer getCustomerByPhone(String phone);
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Long id, Customer customer);
    void deleteCustomer(Long id);
    void updateCustomerLevel(Long id, String level);
    List<Customer> getCustomersByLevel(String level);
    Map<String, Object> getCustomerStats(Long customerId);
    List<Customer> getChurnWarningCustomers();
}