package com.nvzhuang.controller;

import com.nvzhuang.entity.Customer;
import com.nvzhuang.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers(@RequestParam(required = false) String level,
                                                       @RequestParam(required = false) String phone) {
        List<Customer> customers;
        if (level != null) {
            customers = customerService.getCustomersByLevel(level);
        } else if (phone != null) {
            Customer customer = customerService.getCustomerByPhone(phone);
            customers = customer != null ? List.of(customer) : List.of();
        } else {
            customers = customerService.getAllCustomers();
        }
        return ResponseEntity.ok(customers);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }
    
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.createCustomer(customer));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer updated = customerService.updateCustomer(id, customer);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/level")
    public ResponseEntity<Customer> updateCustomerLevel(@PathVariable Long id, @RequestBody Map<String, String> body) {
        customerService.updateCustomerLevel(id, body.get("level"));
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
    
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getCustomerStats(@PathVariable Long id) {
        Map<String, Object> stats = customerService.getCustomerStats(id);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/churn-warning")
    public ResponseEntity<List<Customer>> getChurnWarningCustomers() {
        return ResponseEntity.ok(customerService.getChurnWarningCustomers());
    }
}