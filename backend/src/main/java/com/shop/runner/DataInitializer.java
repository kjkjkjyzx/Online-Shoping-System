package com.shop.runner;

import com.shop.entity.Category;
import com.shop.entity.Product;
import com.shop.entity.User;
import com.shop.service.CategoryService;
import com.shop.service.ProductService;
import com.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 数据初始化 Runner
 * 应用启动时自动执行，初始化测试数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        System.out.println("=== 开始初始化测试数据 ===");

        // 初始化用户数据
        initUsers();

        // 初始化分类数据
        initCategories();

        // 初始化商品数据
        initProducts();

        System.out.println("=== 测试数据初始化完成 ===");
    }

    /**
     * 初始化用户数据
     */
    private void initUsers() {
        if (userService.count() > 0) {
            System.out.println("用户数据已存在，跳过初始化");
            return;
        }

        // 创建管理员账号
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setNickname("管理员");
        admin.setEmail("admin@shop.com");
        admin.setPhone("13800138000");
        admin.setStatus(1);
        admin.setRole(0); // 管理员
        userService.save(admin);

        // 创建测试用户
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setNickname("测试用户 1");
        user1.setEmail("user1@shop.com");
        user1.setPhone("13800138001");
        user1.setStatus(1);
        user1.setRole(1);
        userService.save(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setNickname("测试用户 2");
        user2.setEmail("user2@shop.com");
        user2.setPhone("13800138002");
        user2.setStatus(1);
        user2.setRole(1);
        userService.save(user2);

        // 创建测试商家
        User merchant = new User();
        merchant.setUsername("merchant1");
        merchant.setPassword(passwordEncoder.encode("123456"));
        merchant.setNickname("测试商家");
        merchant.setEmail("merchant1@shop.com");
        merchant.setPhone("13800138003");
        merchant.setStatus(1);
        merchant.setRole(2); // 商家
        userService.save(merchant);

        System.out.println("用户数据初始化完成");
    }

    /**
     * 初始化分类数据
     */
    private void initCategories() {
        if (categoryService.count() > 0) {
            System.out.println("分类数据已存在，跳过初始化");
            return;
        }

        String[][] categories = {
            {"服装", "1"},
            {"数码", "2"},
            {"家居", "3"},
            {"美妆", "4"},
            {"食品", "5"},
            {"图书", "6"}
        };

        for (int i = 0; i < categories.length; i++) {
            Category category = new Category();
            category.setName(categories[i][0]);
            category.setParentId(0L);
            category.setLevel(1);
            category.setSortOrder(i + 1);
            category.setStatus(1);
            categoryService.save(category);
        }

        System.out.println("分类数据初始化完成");
    }

    /**
     * 初始化商品数据
     */
    private void initProducts() {
        if (productService.count() > 0) {
            System.out.println("商品数据已存在，跳过初始化");
            return;
        }

        Object[][] products = {
            {"春季新款 T 恤", "舒适纯棉，多色可选", 99.00, 1000, 500, 1L},
            {"无线蓝牙耳机", "降噪耳机，长续航", 299.00, 500, 300, 2L},
            {"北欧风格台灯", "简约设计，护眼 LED", 159.00, 300, 200, 3L},
            {"保湿面膜", "深层补水，10 片装", 89.00, 800, 600, 4L},
            {"进口巧克力", "比利时进口，72% 可可", 59.00, 1500, 1000, 5L},
            {"Java 编程思想", "经典编程书籍", 89.00, 200, 150, 6L},
            {"运动跑鞋", "轻便透气，减震舒适", 199.00, 600, 400, 1L},
            {"智能手表", "心率监测，运动追踪", 599.00, 300, 200, 2L},
            {"四件套床品", "纯棉材质，亲肤舒适", 299.00, 400, 250, 3L},
            {"口红套装", "正品保证，持久不脱色", 169.00, 500, 350, 4L},
            {"啵果礼盒", "精选进口啵果，营养丰富", 128.00, 800, 600, 5L},
            {"Python 入门教程", "零基础学编程", 59.00, 300, 200, 6L}
        };

        for (Object[] prod : products) {
            Product product = new Product();
            product.setName((String) prod[0]);
            product.setDescription((String) prod[1]);
            product.setPrice(BigDecimal.valueOf((Double) prod[2]));
            product.setStock((Integer) prod[3]);
            product.setSales((Integer) prod[4]);
            product.setCategoryId((Long) prod[5]);
            product.setStatus(1);
            product.setImages("[\"img1.jpg\",\"img2.jpg\"]");
            productService.save(product);
        }

        System.out.println("商品数据初始化完成");
    }
}
