package com.nvzhuang.service;

import java.util.Map;

public interface SystemConfigService {
    String getConfigValue(String key);
    void setConfigValue(String key, String value);
    Map<String, String> getAllConfigs();
    String getCompanyName();
    void setCompanyName(String name);
}