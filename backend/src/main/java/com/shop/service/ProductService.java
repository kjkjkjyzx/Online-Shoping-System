package com.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表
     */
    Page<Product> getPageList(Integer pageNum, Integer pageSize, Long categoryId, String keyword);

    /**
     * 搜索商品
     */
    List<Product> searchProducts(String keyword);

    /**
     * 根据分类 ID 获取商品列表
     */
    List<Product> getListByCategory(Long categoryId);

    /**
     * 获取指定商家的全部商品（含下架），用于商家后台管理
     * @param merchantId 商家用户 ID；为 null 时返回所有商品（管理员使用）
     */
    List<Product> getMerchantProducts(Long merchantId);

    /**
     * 获取热销商品
     */
    List<Product> getHotProducts(Integer limit);
}
