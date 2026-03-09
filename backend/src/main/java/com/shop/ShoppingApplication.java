package com.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网上购物网站系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.shop.mapper")
public class ShoppingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingApplication.class, args);
        System.out.println("=================================");
        System.out.println("网上购物网站系统启动成功！");
        System.out.println("=================================");
    }
}
