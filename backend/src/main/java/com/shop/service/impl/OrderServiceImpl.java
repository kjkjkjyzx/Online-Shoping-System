package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.BusinessException;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.mapper.OrderItemMapper;
import com.shop.mapper.OrderMapper;
import com.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Order order, List<OrderItem> orderItems) {
        // 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis() + (int) (Math.random() * 10000);
        order.setOrderNo(orderNo);
        order.setStatus(0); // 待支付
        order.setCreateTime(LocalDateTime.now());

        // 填充 NOT NULL 默认值
        if (order.getPayAmount() == null) {
            order.setPayAmount(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
        }
        if (order.getFreightAmount() == null) {
            order.setFreightAmount(BigDecimal.ZERO);
        }
        if (order.getTotalAmount() == null) {
            order.setTotalAmount(BigDecimal.ZERO);
        }
        if (order.getReceiverName() == null) {
            order.setReceiverName("待填写");
        }
        if (order.getReceiverPhone() == null) {
            order.setReceiverPhone("待填写");
        }
        if (order.getReceiverAddress() == null) {
            order.setReceiverAddress("待填写");
        }

        // 保存订单
        this.save(order);

        // 保存订单明细
        List<OrderItem> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                item.setOrderId(order.getId());
                orderItemMapper.insert(item);
            }
        }

        return order;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "只有待支付订单才能取消");
        }
        order.setStatus(8); // 已取消
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(400, "只有待收货订单才能确认");
        }
        order.setStatus(3); // 已完成
        order.setConfirmTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(400, "只有待发货订单才能发货");
        }
        order.setStatus(2); // 待收货
        order.setDeliveryTime(LocalDateTime.now());
        return this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "只有待支付订单才能支付");
        }
        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());
        return this.updateById(order);
    }
}
