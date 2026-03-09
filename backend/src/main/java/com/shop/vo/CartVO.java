package com.shop.vo;

import com.shop.entity.Cart;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车视图对象，包含商品信息
 */
@Data
public class CartVO extends Cart {

    /** 商品名称 */
    private String name;

    /** 商品图片 */
    private String image;

    /** 商品价格 */
    private BigDecimal price;
}
