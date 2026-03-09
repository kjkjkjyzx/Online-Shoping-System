package com.shop.controller;

import com.shop.common.Result;
import com.shop.entity.Cart;
import com.shop.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.shop.vo.CartVO;

import com.shop.common.RequireRole;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/cart")
@Tag(name = "购物车管理接口")
@RequireRole({1}) // 仅普通用户可访问购物车
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @Operation(summary = "获取购物车列表")
    public Result<List<CartVO>> getCartList(@RequestParam Long userId) {
        List<CartVO> list = cartService.getCartListByUserId(userId);
        return Result.success(list);
    }

    @PostMapping("/items")
    @Operation(summary = "添加到购物车")
    public Result<Boolean> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        boolean success = cartService.addToCart(userId, productId, quantity);
        return success ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "更新购物车商品数量")
    public Result<Boolean> updateQuantity(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        boolean success = cartService.updateQuantity(id, quantity);
        return success ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "删除购物车商品")
    public Result<Boolean> deleteCart(@PathVariable Long id) {
        boolean success = cartService.removeById(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @DeleteMapping
    @Operation(summary = "清空购物车")
    public Result<Boolean> clearCart(@RequestParam Long userId) {
        boolean success = cartService.clearCart(userId);
        return success ? Result.success("清空成功", true) : Result.error("清空失败");
    }
}
