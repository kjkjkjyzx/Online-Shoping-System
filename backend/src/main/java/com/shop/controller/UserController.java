package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.common.BusinessException;
import com.shop.common.RequireRole;
import com.shop.common.Result;
import com.shop.entity.User;
import com.shop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    @RequireRole({0, 1, 2})
    @Operation(summary = "获取用户信息")
    public Result<User> getUserInfo(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        Integer role = getCurrentRole(request);
        if (!id.equals(currentUserId) && role != 0) {
            throw new BusinessException(403, "无权限查看该用户信息");
        }

        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return Result.success(user);
    }

    @PutMapping("/{id}")
    @RequireRole({0, 1, 2})
    @Operation(summary = "更新用户信息")
    public Result<Boolean> updateUser(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        Long currentUserId = getCurrentUserId(request);
        Integer role = getCurrentRole(request);
        if (!id.equals(currentUserId) && role != 0) {
            throw new BusinessException(403, "无权限修改该用户信息");
        }

        if (userService.getById(id) == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 仅允许修改基础资料，防止越权修改角色/状态等敏感字段
        User update = new User();
        update.setId(id);
        update.setNickname(user.getNickname());
        update.setEmail(user.getEmail());
        update.setPhone(user.getPhone());
        update.setAvatar(user.getAvatar());
        update.setGender(user.getGender());

        boolean success = userService.updateById(update);
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
        if (role == null || (role != 0 && role != 1 && role != 2)) {
            throw new BusinessException(400, "非法角色值");
        }
        if (userService.getById(id) == null) {
            throw new BusinessException(404, "用户不存在");
        }

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
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(400, "非法状态值");
        }
        if (userService.getById(id) == null) {
            throw new BusinessException(404, "用户不存在");
        }

        User user = new User();
        user.setId(id);
        user.setStatus(status);
        boolean success = userService.updateById(user);
        return success ? Result.success("状态修改成功", true) : Result.error("状态修改失败");
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }

    private Integer getCurrentRole(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        if (role == null) {
            throw new BusinessException(401, "请先登录");
        }
        return role;
    }
}
