package com.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单 Mapper 接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询包含指定商家商品的订单
     * 通过 order_item.product_id → product.merchant_id 关联过滤
     */
    @Select("<script>" +
            "SELECT DISTINCT o.*, u.username, u.phone AS user_phone FROM orders o " +
            "INNER JOIN order_item oi ON oi.order_id = o.id " +
            "INNER JOIN product p ON p.id = oi.product_id " +
            "LEFT JOIN user u ON u.id = o.user_id " +
            "WHERE p.merchant_id = #{merchantId} " +
            "AND o.deleted = 0 " +
            "<if test='status != null'>AND o.status = #{status} </if>" +
            "ORDER BY o.create_time DESC" +
            "</script>")
    List<Order> selectMerchantOrders(@Param("merchantId") Long merchantId,
                                     @Param("status") Integer status);
}
