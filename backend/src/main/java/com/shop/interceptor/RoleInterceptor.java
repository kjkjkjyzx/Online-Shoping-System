package com.shop.interceptor;

import com.shop.common.BusinessException;
import com.shop.common.RequireRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 角色权限拦截器
 * 在 JwtInterceptor 之后执行，校验接口的角色权限
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 只处理 Controller 方法
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 优先取方法级注解，没有则取类级注解
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        // 无角色注解，不限制
        if (requireRole == null) {
            return true;
        }

        // 必须登录
        Integer role = (Integer) request.getAttribute("role");
        if (role == null) {
            throw new BusinessException(401, "请先登录");
        }

        // 检查角色是否匹配
        final int roleValue = role;
        boolean allowed = Arrays.stream(requireRole.value()).anyMatch(r -> r == roleValue);
        if (!allowed) {
            throw new BusinessException(403, "权限不足，无法访问该接口");
        }

        return true;
    }
}
