package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.RequireRole;
import com.shop.common.Result;
import com.shop.entity.User;
import com.shop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
@Tag(name = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Boolean> register(@RequestBody User user) {
        boolean success = userService.register(user);
        return success ? Result.success("注册成功", true) : Result.error("注册失败");
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<String> login(@RequestBody User user) {
        String token = userService.login(user.getUsername(), user.getPassword());
        return Result.success(token);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户信息")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息")
    public Result<Boolean> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateById(user);
        return success ? Result.success("更新成功", true) : Result.error("更新失败");
    }

    @GetMapping
    @RequireRole({0}) // 仅管理员
    @Operation(summary = "获取用户列表（管理员）")
    public Result<IPage<User>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword).or().like(User::getNickname, keyword);
        }
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        IPage<User> page = userService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    @PutMapping("/{id}/role")
    @RequireRole({0}) // 仅管理员
    @Operation(summary = "修改用户角色（管理员）")
    public Result<Boolean> updateUserRole(@PathVariable Long id, @RequestParam Integer role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        boolean success = userService.updateById(user);
        return success ? Result.success("角色修改成功", true) : Result.error("角色修改失败");
    }

    @PutMapping("/{id}/status")
    @RequireRole({0}) // 仅管理员
    @Operation(summary = "修改用户状态（管理员）")
    public Result<Boolean> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        boolean success = userService.updateById(user);
        return success ? Result.success("状态修改成功", true) : Result.error("状态修改失败");
    }
}

