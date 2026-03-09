package com.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.entity.Category;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取所有分类
     */
    List<Category> getAllCategories();

    /**
     * 获取分类树
     */
    List<Category> getCategoryTree();
}
