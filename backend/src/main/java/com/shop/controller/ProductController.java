package com.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.RequireRole;
import com.shop.common.Result;
import com.shop.entity.Product;
import com.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/products")
@Tag(name = "商品管理接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "获取商品列表")
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        Page<Product> page = productService.getPageList(pageNum, pageSize, categoryId, keyword);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getById(id);
        return Result.success(product);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索商品")
    public Result<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> list = productService.searchProducts(keyword);
        return Result.success(list);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热销商品")
    public Result<List<Product>> getHotProducts(
            @RequestParam(defaultValue = "8") Integer limit) {
        List<Product> list = productService.getHotProducts(limit);
        return Result.success(list);
    }

    @GetMapping("/mine")
    @RequireRole({0, 2})
    @Operation(summary = "获取商家自己的商品（商家：仅自己；管理员：可按 merchantId 筛选或查全部）")
    public Result<List<Product>> getMerchantProducts(
            @RequestParam(required = false) Long merchantId,
            HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role != null && role == 2) {
            // 商家只能查自己的商品
            Long userId = (Long) request.getAttribute("userId");
            return Result.success(productService.getMerchantProducts(userId));
        }
        // 管理员：merchantId 不为 null 时按商家过滤，否则返回全部
        return Result.success(productService.getMerchantProducts(merchantId));
    }

    @PostMapping
    @RequireRole({0, 2}) // 管理员或商家
    @Operation(summary = "创建商品")
    public Result<Boolean> createProduct(@RequestBody Product product, HttpServletRequest request) {
        // 如果是商家，自动绑定 merchantId
        Integer role = (Integer) request.getAttribute("role");
        if (role != null && role == 2) {
            Long userId = (Long) request.getAttribute("userId");
            product.setMerchantId(userId);
        }
        product.setId(null); // 防止前端传入 id 导致主键冲突，强制使用数据库自增
        boolean success = productService.save(product);
        return success ? Result.success("创建成功", true) : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    @RequireRole({0, 2}) // 管理员或商家
    @Operation(summary = "更新商品")
    public Result<Boolean> updateProduct(@PathVariable Long id, @RequestBody Product product,
                                         HttpServletRequest request) {
        product.setId(id);
        Integer role = (Integer) request.getAttribute("role");
        // 商家只能修改自己的商品
        if (role != null && role == 2) {
            Long userId = (Long) request.getAttribute("userId");
            Product existing = productService.getById(id);
            if (existing == null || !userId.equals(existing.getMerchantId())) {
                return Result.error("无权限修改该商品");
            }
        }
        boolean success = productService.updateById(product);
        return success ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    @RequireRole({0, 2}) // 管理员或商家
    @Operation(summary = "删除商品")
    public Result<Boolean> deleteProduct(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role != null && role == 2) {
            Long userId = (Long) request.getAttribute("userId");
            Product existing = productService.getById(id);
            if (existing == null || !userId.equals(existing.getMerchantId())) {
                return Result.error("无权限删除该商品");
            }
        }
        boolean success = productService.removeById(id);
        return success ? Result.success("删除成功", true) : Result.error("删除失败");
    }
}
