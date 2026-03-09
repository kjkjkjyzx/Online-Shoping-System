package com.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.entity.Cart;
import com.shop.vo.CartVO;

import java.util.List;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加商品到购物车
     */
    boolean addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车商品数量
     */
    boolean updateQuantity(Long id, Integer quantity);

    /**
     * 获取用户购物车列表（含商品信息）
     */
    List<CartVO> getCartListByUserId(Long userId);

    /**
     * 清空购物车
     */
    boolean clearCart(Long userId);
}
