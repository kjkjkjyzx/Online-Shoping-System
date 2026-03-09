package com.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
}
