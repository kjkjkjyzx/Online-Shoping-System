package com.shop.common;

/**
 * 角色枚举 0-管理员 1-普通用户 2-商家
 */
public enum RoleEnum {
    ADMIN(0), USER(1), MERCHANT(2);

    private final int value;

    RoleEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
