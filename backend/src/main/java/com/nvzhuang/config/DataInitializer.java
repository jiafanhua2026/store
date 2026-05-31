package com.nvzhuang.config;

import com.nvzhuang.entity.*;
import com.nvzhuang.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductSkuRepository productSkuRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private SystemConfigRepository systemConfigRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setName("管理员");
            admin.setRole("admin");
            userRepository.save(admin);
            
            User sales = new User();
            sales.setUsername("sales");
            sales.setPassword("sales123");
            sales.setName("销售员");
            sales.setRole("sales");
            userRepository.save(sales);
        }
        
        if (systemConfigRepository.findByConfigKey("company_name").isEmpty()) {
            SystemConfig config = new SystemConfig();
            config.setConfigKey("company_name");
            config.setConfigValue("等你服饰");
            config.setDescription("公司名称");
            systemConfigRepository.save(config);
        }
        
        if (productRepository.count() == 0) {
            createSampleProducts();
        }
        
        if (customerRepository.count() == 0) {
            createSampleCustomers();
        }
    }
    
    private void createSampleProducts() {
        String[] categories = {"上衣", "裤子", "裙子", "配饰", "套装"};
        String[] sizes = {"S", "M", "L", "XL"};
        String[] colors = {"白色", "黑色", "粉色", "蓝色", "绿色"};
        String[][] products = {
            {"蕾丝连衣裙", "裙子", "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=elegant%20white%20lace%20dress%20fashion%20photography&image_size=portrait_4_3", "优雅蕾丝设计，修身版型", "120", "299", "优雅", "蕾丝", "夏季"},
            {"韩版T恤", "上衣", "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=korean%20style%20white%20t-shirt%20fashion&image_size=portrait_4_3", "简约韩版设计", "35", "89", "简约", "棉质", "春夏"},
            {"高腰牛仔裤", "裤子", "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=high%20waist%20blue%20jeans%20fashion&image_size=portrait_4_3", "高腰显瘦设计", "60", "159", "显瘦", "牛仔布", "四季"},
            {"时尚围巾", "配饰", "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=fashion%20silk%20scarf%20elegant&image_size=portrait_4_3", "真丝材质，优雅大方", "45", "128", "优雅", "真丝", "春秋"},
            {"职业套装", "套装", "https://neeko-copilot.bytedance.net/api/text_to_image?prompt=professional%20women%20business%20suit&image_size=portrait_4_3", "职业干练，气质优雅", "180", "459", "职业", "西装料", "四季"},
        };
        
        for (String[] p : products) {
            Product product = new Product();
            product.setName(p[0]);
            product.setCategory(p[1]);
            product.setImage(p[2]);
            product.setDetail(p[3]);
            product.setCostPrice(new BigDecimal(p[4]));
            product.setSalePrice(new BigDecimal(p[5]));
            product.setStyle(p[6]);
            product.setFabric(p[7]);
            product.setSeason(p[8]);
            product.setStatus(1);
            Product saved = productRepository.save(product);
            
            for (String size : sizes) {
                for (String color : colors) {
                    ProductSku sku = new ProductSku();
                    sku.setProductId(saved.getId());
                    sku.setSize(size);
                    sku.setColor(color);
                    sku.setStock(10 + (int)(Math.random() * 20));
                    productSkuRepository.save(sku);
                }
            }
        }
    }
    
    private void createSampleCustomers() {
        String[][] customers = {
            {"张三", "13800138001", "zhangsan", "北京市朝阳区", "VIP"},
            {"李四", "13800138002", "lisi", "上海市浦东新区", "普通"},
            {"王五", "13800138003", "wangwu", "广州市天河区", "VIP"},
            {"赵六", "13800138004", "zhaoliu", "深圳市南山区", "普通"},
            {"钱七", "13800138005", "qianqi", "杭州市西湖区", "普通"},
        };
        
        for (String[] c : customers) {
            Customer customer = new Customer();
            customer.setName(c[0]);
            customer.setPhone(c[1]);
            customer.setWechat(c[2]);
            customer.setAddress(c[3]);
            customer.setLevel(c[4]);
            customer.setTotalOrder((int)(Math.random() * 10));
            customer.setTotalAmount(new BigDecimal(String.valueOf((int)(Math.random() * 5000))));
            customerRepository.save(customer);
        }
    }
}