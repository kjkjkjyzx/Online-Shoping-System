package com.shop.interceptor;

import com.shop.common.BusinessException;
import com.shop.config.JwtProperties;
import com.shop.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT 拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取 Token
        String token = request.getHeader(jwtProperties.getHeader());

        if (!StringUtils.hasText(token)) {
            token = request.getParameter("token");
        }

        if (!StringUtils.hasText(token)) {
            // 如果没有 Token，放行（由具体业务判断是否需要登录）
            return true;
        }

        // 去掉 Bearer 前缀
        String tokenPrefix = jwtProperties.getPrefix() + " ";
        if (token.startsWith(tokenPrefix)) {
            token = token.substring(tokenPrefix.length());
        }

        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }

        // 将用户 ID 和角色存入请求属性
        Long userId = jwtUtil.getUserIdFromToken(token);
        Integer role = jwtUtil.getRoleFromToken(token);
        request.setAttribute("userId", userId);
        request.setAttribute("role", role);

        return true;
    }
}
