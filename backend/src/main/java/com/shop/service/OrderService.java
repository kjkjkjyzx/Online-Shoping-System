package com.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Order createOrder(Order order, List<OrderItem> orderItems);

    /**
     * 根据订单号查询订单
     */
    Order getByOrderNo(String orderNo);

    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);

    /**
     * 确认收货
     */
    boolean confirmOrder(Long orderId);

    /**
     * 发货（商家/管理员）
     */
    boolean shipOrder(Long orderId);

    /**
     * 模拟支付（待支付 → 待发货）
     */
    boolean payOrder(Long orderId);
}
