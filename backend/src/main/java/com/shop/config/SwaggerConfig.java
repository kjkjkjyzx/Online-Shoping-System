package com.shop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("网上购物网站系统 API 文档")
                        .description("基于 Spring Boot + Vue 的网上购物网站系统")
                .contact(new Contact()
                    .name("姜磊")
                    .url("")
                    .email(""))
                        .version("1.0.0"));
    }
}
