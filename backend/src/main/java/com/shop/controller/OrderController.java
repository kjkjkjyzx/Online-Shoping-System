package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.RequireRole;
import com.shop.common.Result;
import com.shop.common.PageResult;
import com.shop.entity.Order;
import com.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/orders")
@Tag(name = "订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private com.shop.mapper.OrderMapper orderMapper;

    @GetMapping
    @Operation(summary = "获取订单列表")
    public Result<List<Order>> getOrderList(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        List<Order> list = orderService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public Result<Order> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }

    @GetMapping("/no/{orderNo}")
    @Operation(summary = "根据订单号获取订单详情")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        return Result.success(order);
    }

    @PostMapping
    @RequireRole({1}) // 普通用户
    @Operation(summary = "创建订单")
    public Result<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order, null);
        return Result.success(created);
    }

    @PutMapping("/{id}/cancel")
    @RequireRole({1}) // 普通用户
    @Operation(summary = "取消订单")
    public Result<Boolean> cancelOrder(@PathVariable Long id) {
        boolean success = orderService.cancelOrder(id);
        return success ? Result.success("取消成功", true) : Result.error("取消失败");
    }

    @PutMapping("/{id}/pay")
    @RequireRole({1}) // 普通用户
    @Operation(summary = "模拟支付（待支付→待发货）")
    public Result<Boolean> payOrder(@PathVariable Long id) {
        boolean success = orderService.payOrder(id);
        return success ? Result.success("支付成功", true) : Result.error("支付失败");
    }

    @PutMapping("/{id}/confirm")
    @RequireRole({1}) // 普通用户
    @Operation(summary = "确认收货")
    public Result<Boolean> confirmOrder(@PathVariable Long id) {
        boolean success = orderService.confirmOrder(id);
        return success ? Result.success("确认成功", true) : Result.error("确认失败");
    }

    @PutMapping("/{id}/ship")
    @RequireRole({0, 2}) // 管理员或商家
    @Operation(summary = "发货（商家/管理员）")
    public Result<Boolean> shipOrder(@PathVariable Long id) {
        boolean success = orderService.shipOrder(id);
        return success ? Result.success("发货成功", true) : Result.error("发货失败");
    }

    @GetMapping("/merchant")
    @RequireRole({0, 2}) // 管理员或商家
    @Operation(summary = "获取商家订单列表（按商品的merchantId）")
    public Result<List<Order>> getMerchantOrders(
            @RequestParam Long merchantId,
            @RequestParam(required = false) Integer status) {
        List<Order> list = orderMapper.selectMerchantOrders(merchantId, status);
        return Result.success(list);
    }

    @GetMapping("/admin")
    @RequireRole({0}) // 仅管理员
    @Operation(summary = "管理员查询全部订单（分页 + 状态 + 关键词）")
    public Result<Page<Order>> getAdminOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Order::getStatus, status);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword)
                    .or().eq(Order::getUserId, keyword.matches("\\d+") ? Long.parseLong(keyword) : -1L));
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> page = orderService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }
}
