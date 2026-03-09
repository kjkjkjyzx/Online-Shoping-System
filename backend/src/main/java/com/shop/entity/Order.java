package com.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.shop.entity.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@TableName("orders")
@Schema(description = "订单实体")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单 ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户 ID")
    private Long userId;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "运费")
    private BigDecimal freightAmount;

    @Schema(description = "订单状态 0-待支付 1-待发货 2-待收货 3-已完成 4-售后处理 5-已关闭 8-已取消")
    private Integer status;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人电话")
    private String receiverPhone;

    @Schema(description = "收货地址")
    private String receiverAddress;

    @Schema(description = "订单备注")
    private String remark;

    @Schema(description = "订单明细（非数据库字段）")
    @TableField(exist = false)
    private List<OrderItem> items;

    @Schema(description = "下单用户名（非数据库字段）")
    @TableField(exist = false)
    private String username;

    @Schema(description = "用户注册手机号（非数据库字段）")
    @TableField(exist = false)
    private String userPhone;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "发货时间")
    private LocalDateTime deliveryTime;

    @Schema(description = "确认收货时间")
    private LocalDateTime confirmTime;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除 0-未删除 1-已删除")
    @TableLogic
    private Integer deleted;
}
