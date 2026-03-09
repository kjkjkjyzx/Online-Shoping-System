package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.BusinessException;
import com.shop.entity.Cart;
import com.shop.entity.Product;
import com.shop.mapper.CartMapper;
import com.shop.service.CartService;
import com.shop.service.ProductService;
import com.shop.vo.CartVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private ProductService productService;

    @Override
    public boolean addToCart(Long userId, Long productId, Integer quantity) {
        // 检查购物车是否已有该商品
        Cart cart = this.getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId));

        if (cart != null) {
            // 已有该商品，数量累加
            cart.setQuantity(cart.getQuantity() + quantity);
            return this.updateById(cart);
        } else {
            // 没有该商品，新增记录
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProductId(productId);
            newCart.setQuantity(quantity);
            newCart.setSelected(1);
            return this.save(newCart);
        }
    }

    @Override
    public boolean updateQuantity(Long id, Integer quantity) {
        Cart cart = this.getById(id);
        if (cart == null) {
            throw new BusinessException(404, "购物车记录不存在");
        }
        cart.setQuantity(quantity);
        return this.updateById(cart);
    }

    @Override
    public List<CartVO> getCartListByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime);
        List<Cart> cartList = this.list(wrapper);

        return cartList.stream().map(cart -> {
            CartVO vo = new CartVO();
            BeanUtils.copyProperties(cart, vo);
            Product product = productService.getById(cart.getProductId());
            if (product != null) {
                vo.setName(product.getName());
                vo.setImage(product.getImages());
                vo.setPrice(product.getPrice());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return this.remove(wrapper);
    }
}
