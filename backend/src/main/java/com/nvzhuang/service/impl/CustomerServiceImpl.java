package com.nvzhuang.service.impl;

import com.nvzhuang.entity.Customer;
import com.nvzhuang.entity.Order;
import com.nvzhuang.repository.CustomerRepository;
import com.nvzhuang.repository.OrderRepository;
import com.nvzhuang.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    
    @Override
    public Customer getCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone).orElse(null);
    }
    
    @Override
    @Transactional
    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    @Override
    @Transactional
    public Customer updateCustomer(Long id, Customer customer) {
        Optional<Customer> existing = customerRepository.findById(id);
        if (existing.isPresent()) {
            Customer c = existing.get();
            c.setName(customer.getName());
            c.setPhone(customer.getPhone());
            c.setWechat(customer.getWechat());
            c.setAddress(customer.getAddress());
            c.setLevel(customer.getLevel());
            return customerRepository.save(c);
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public void updateCustomerLevel(Long id, String level) {
        Optional<Customer> existing = customerRepository.findById(id);
        if (existing.isPresent()) {
            Customer c = existing.get();
            c.setLevel(level);
            customerRepository.save(c);
        }
    }
    
    @Override
    public List<Customer> getCustomersByLevel(String level) {
        return customerRepository.findByLevel(level);
    }
    
    @Override
    public Map<String, Object> getCustomerStats(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return null;
        }
        
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("customer", customer);
        stats.put("orders", orders);
        stats.put("orderCount", orders.size());
        stats.put("totalAmount", customer.getTotalAmount());
        
        return stats;
    }
    
    @Override
    public List<Customer> getChurnWarningCustomers() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(3);
        return customerRepository.findAll().stream()
                .filter(c -> c.getLastPurchaseTime() != null && c.getLastPurchaseTime().isBefore(threshold))
                .collect(Collectors.toList());
    }
}