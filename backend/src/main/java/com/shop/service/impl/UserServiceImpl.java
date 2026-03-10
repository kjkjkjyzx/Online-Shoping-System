package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.common.BusinessException;
import com.shop.entity.User;
import com.shop.mapper.UserMapper;
import com.shop.service.UserService;
import com.shop.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${shop.admin-secret}")
    private String adminSecret;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean register(User user) {
        // 参数校验
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BusinessException(400, "密码不能为空");
        }

        // 管理员注册需要校验密钥
        if (Integer.valueOf(0).equals(user.getRole())) {
            if (user.getAdminSecret() == null || !adminSecret.equals(user.getAdminSecret())) {
                throw new BusinessException(403, "管理员密钥不正确");
            }
        }

        // 检查用户名是否存在
        Long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        if (count > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 新注册统一使用 BCrypt
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);

        // 保留前端传入的 role（0=管理员 1=普通用户 2=商家），非法值归为普通用户
        if (user.getRole() == null || (user.getRole() != 0 && user.getRole() != 1 && user.getRole() != 2)) {
            user.setRole(1);
        }
        return this.save(user);
    }

    @Override
    public String login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }

        // 查询用户
        User user = this.getByUsername(username);
        if (user == null) {
            throw new BusinessException(400, "用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(403, "用户已被禁用");
        }

        // 验证密码（支持旧 MD5 兼容）
        if (!matchesPassword(password, user.getPassword())) {
            throw new BusinessException(400, "密码错误");
        }

        // 若命中旧 MD5，登录成功后升级为 BCrypt
        if (isLegacyMd5Hash(user.getPassword())) {
            User update = new User();
            update.setId(user.getId());
            update.setPassword(passwordEncoder.encode(password));
            this.updateById(update);
        }

        // 生成 JWT Token
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public User getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        // 新密码：BCrypt
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        // 旧密码：MD5（兼容迁移）
        String md5Password = DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
        return storedPassword.equalsIgnoreCase(md5Password);
    }

    private boolean isLegacyMd5Hash(String hash) {
        return hash != null && hash.matches("^[a-fA-F0-9]{32}$");
    }
}
