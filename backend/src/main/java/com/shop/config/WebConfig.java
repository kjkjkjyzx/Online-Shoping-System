package com.shop.config;

import com.shop.interceptor.JwtInterceptor;
import com.shop.interceptor.RoleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Autowired
    private RoleInterceptor roleInterceptor;

    private static final String[] EXCLUDE_PATHS = {
            "/users/login",
            "/users/register",
            // 注意：/products/** 和 /categories/** 不排除，
            //   JWT 拦截器在无 Token 时会自动放行（公开 GET 接口正常可用），
            //   有 Token 时解析 userId/role，保证 @RequireRole 和商家隔离逻辑正确执行
            "/doc.html",
            "/webjars/**",
            "/swagger-resources/**",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 拦截器：验证 Token 并解析用户信息
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS)
                .order(1);

        // 角色拦截器：检查 @RequireRole 注解
        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS)
                .order(2);
    }
}
