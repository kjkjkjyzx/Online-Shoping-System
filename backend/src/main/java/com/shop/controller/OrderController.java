package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.BusinessException;
import com.shop.common.RequireRole;
import com.shop.common.Result;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.mapper.OrderItemMapper;
import com.shop.mapper.OrderMapper;
import com.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @GetMapping
    @RequireRole({1})
    @Operation(summary = "获取订单列表")
    public Result<List<Order>> getOrderList(
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, currentUserId);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        List<Order> list = orderService.list(wrapper);
        fillOrderItems(list);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @RequireRole({1})
    @Operation(summary = "获取订单详情")
    public Result<Order> getOrderDetail(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        Order order = assertOrderOwner(id, currentUserId);
        fillOrderItems(order);
        return Result.success(order);
    }

    @GetMapping("/no/{orderNo}")
    @RequireRole({1})
    @Operation(summary = "根据订单号获取订单详情")
    public Result<Order> getOrderByOrderNo(@PathVariable String orderNo, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);

        Order order = orderService.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!currentUserId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权限查看该订单");
        }

        fillOrderItems(order);
        return Result.success(order);
    }

    @PostMapping
    @RequireRole({1}) // 普通用户
    @Operation(summary = "创建订单")
    public Result<Order> createOrder(@RequestBody Order order, HttpServletRequest request) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException(400, "订单商品不能为空");
        }

        Long currentUserId = getCurrentUserId(request);
        order.setUserId(currentUserId);

        Order created = orderService.createOrder(order, order.getItems());
        fillOrderItems(created);
        return Result.success(created);
    }

    @PutMapping("/{id}/cancel")
    @RequireRole({1}) // 普通用户
    @Operation(summary = "取消订单")
    public Result<Boolean> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        assertOrderOwner(id, currentUserId);

        boolean success = orderService.cancelOrder(id);
        return success ? Result.success("取消成功", true) : Result.error("取消失败");
    }

    @PutMapping("/{id}/pay")
    @RequireRole({1}) // 普通用户
    @Operation(summary = "模拟支付（待支付→待发货）")
    public Result<Boolean> payOrder(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        assertOrderOwner(id, currentUserId);

        boolean success = orderService.payOrder(id);
        return success ? Result.success("支付成功", true) : Result.error("支付失败");
    }

    @PutMapping("/{id}/confirm")
    @RequireRole({1}) // 普通用户
    @Operation(summary = "确认收货")
    public Result<Boolean> confirmOrder(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        assertOrderOwner(id, currentUserId);

        boolean success = orderService.confirmOrder(id);
        return success ? Result.success("确认成功", true) : Result.error("确认失败");
    }

    @PutMapping("/{id}/ship")
    @RequireRole({0, 2}) // 管理员或商家
    @Operation(summary = "发货（商家/管理员）")
    public Result<Boolean> shipOrder(@PathVariable Long id, HttpServletRequest request) {
        Integer role = getCurrentRole(request);

        Order order = orderService.getById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        if (role == 2) {
            Long merchantId = getCurrentUserId(request);
            boolean ownedByMerchant = orderMapper.countMerchantOrder(id, merchantId) > 0;
            if (!ownedByMerchant) {
                throw new BusinessException(403, "无权限操作该订单");
            }
        }

        boolean success = orderService.shipOrder(id);
        return success ? Result.success("发货成功", true) : Result.error("发货失败");
    }

    @GetMapping("/merchant")
    @RequireRole({0, 2}) // 管理员或商家
    @Operation(summary = "获取商家订单列表（按商品的merchantId）")
    public Result<List<Order>> getMerchantOrders(
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Integer role = getCurrentRole(request);

        Long targetMerchantId;
        if (role == 2) {
            targetMerchantId = getCurrentUserId(request);
        } else {
            if (merchantId == null) {
                throw new BusinessException(400, "管理员查询商家订单时请提供 merchantId");
            }
            targetMerchantId = merchantId;
        }

        List<Order> list = orderMapper.selectMerchantOrders(targetMerchantId, status);
        fillOrderItems(list);
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
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword)
                    .or().eq(Order::getUserId, keyword.matches("\\d+") ? Long.parseLong(keyword) : -1L));
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> page = orderService.page(new Page<>(pageNum, pageSize), wrapper);
        fillOrderItems(page.getRecords());
        return Result.success(page);
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }

    private Integer getCurrentRole(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null) {
            throw new BusinessException(401, "请先登录");
        }
        return role;
    }

    private Order assertOrderOwner(Long orderId, Long currentUserId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!currentUserId.equals(order.getUserId())) {
            throw new BusinessException(403, "无权限操作该订单");
        }
        return order;
    }

    private void fillOrderItems(Order order) {
        if (order == null || order.getId() == null) {
            return;
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        order.setItems(items);
    }

    private void fillOrderItems(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .filter(Objects::nonNull)
                .toList();
        if (orderIds.isEmpty()) {
            return;
        }

        List<OrderItem> allItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds));
        Map<Long, List<OrderItem>> grouped = allItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId));

        for (Order order : orders) {
            order.setItems(grouped.getOrDefault(order.getId(), Collections.emptyList()));
        }
    }
}

