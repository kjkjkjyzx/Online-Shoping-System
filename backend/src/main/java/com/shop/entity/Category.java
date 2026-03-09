package com.shop.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 */
@Data
@TableName("category")
@Schema(description = "商品分类实体")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类 ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父分类 ID")
    private Long parentId;

    @Schema(description = "层级")
    private Integer level;

    @Schema(description = "图标 URL")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态 0-禁用 1-正常")
    private Integer status;

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
