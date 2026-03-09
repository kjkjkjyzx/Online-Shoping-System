package com.shop.common;

import java.lang.annotation.*;

/**
 * 接口角色权限注解
 * 被标注的接口只允许指定角色访问
 * 不加此注解的接口仅要求登录（有 Token），无角色限制
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 允许访问的角色值，对应 RoleEnum.getValue()
     * 例如：@RequireRole({0}) 仅管理员，@RequireRole({0, 2}) 管理员或商家
     */
    int[] value();
}
