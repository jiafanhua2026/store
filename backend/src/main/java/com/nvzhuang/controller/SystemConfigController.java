package com.nvzhuang.controller;

import com.nvzhuang.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class SystemConfigController {
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @GetMapping("/company-name")
    public ResponseEntity<Map<String, String>> getCompanyName() {
        return ResponseEntity.ok(Map.of("companyName", systemConfigService.getCompanyName()));
    }
    
    @PutMapping("/company-name")
    public ResponseEntity<Map<String, String>> setCompanyName(@RequestBody Map<String, String> body) {
        systemConfigService.setCompanyName(body.get("companyName"));
        return ResponseEntity.ok(Map.of("companyName", systemConfigService.getCompanyName()));
    }
    
    @GetMapping
    public ResponseEntity<Map<String, String>> getAllConfigs() {
        return ResponseEntity.ok(systemConfigService.getAllConfigs());
    }
    
    @PostMapping
    public ResponseEntity<Void> setConfig(@RequestBody Map<String, String> body) {
        systemConfigService.setConfigValue(body.get("key"), body.get("value"));
        return ResponseEntity.ok().build();
    }
}