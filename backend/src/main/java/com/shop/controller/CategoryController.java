package com.shop.controller;

import com.shop.common.RequireRole;
import com.shop.common.Result;
import com.shop.entity.Category;
import com.shop.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类控制器
 */
@RestController
@RequestMapping("/categories")
@Tag(name = "商品分类接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取所有分类")
    public Result<List<Category>> getAllCategories() {
        List<Category> list = categoryService.getAllCategories();
        return Result.success(list);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取分类树")
    public Result<List<Category>> getCategoryTree() {
        List<Category> list = categoryService.getCategoryTree();
        return Result.success(list);
    }

    @PostMapping
    @RequireRole({0}) // 仅管理员
    @Operation(summary = "创建分类（管理员）")
    public Result<Boolean> createCategory(@RequestBody Category category) {
        boolean success = categoryService.save(category);
        return success ? Result.success("创建成功", true) : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    @RequireRole({0}) // 仅管理员
    @Operation(summary = "更新分类（管理员）")
    public Result<Boolean> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        boolean success = categoryService.updateById(category);
        return success ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @RequireRole({0}) // 仅管理员
    @Operation(summary = "删除分类（管理员）")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        boolean success = categoryService.removeById(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }
}
