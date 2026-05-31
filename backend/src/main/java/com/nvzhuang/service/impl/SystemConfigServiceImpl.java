package com.nvzhuang.service.impl;

import com.nvzhuang.entity.SystemConfig;
import com.nvzhuang.repository.SystemConfigRepository;
import com.nvzhuang.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    private static final String COMPANY_NAME_KEY = "company_name";
    
    @Override
    public String getConfigValue(String key) {
        return systemConfigRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(null);
    }
    
    @Override
    @Transactional
    public void setConfigValue(String key, String value) {
        Optional<SystemConfig> existing = systemConfigRepository.findByConfigKey(key);
        if (existing.isPresent()) {
            SystemConfig config = existing.get();
            config.setConfigValue(value);
            systemConfigRepository.save(config);
        } else {
            SystemConfig config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            systemConfigRepository.save(config);
        }
    }
    
    @Override
    public Map<String, String> getAllConfigs() {
        Map<String, String> configs = new HashMap<>();
        List<SystemConfig> all = systemConfigRepository.findAll();
        all.forEach(c -> configs.put(c.getConfigKey(), c.getConfigValue()));
        return configs;
    }
    
    @Override
    public String getCompanyName() {
        String name = getConfigValue(COMPANY_NAME_KEY);
        return name != null ? name : "等你服饰";
    }
    
    @Override
    @Transactional
    public void setCompanyName(String name) {
        setConfigValue(COMPANY_NAME_KEY, name);
    }
}