package com.zzy.logintest.service.impl;

import com.zzy.logintest.domain.dto.RegisterRequest;

import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.ApiResponse;

import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.service.UserService;
import com.zzy.logintest.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

   @Override
    @Transactional
    public void register(RegisterRequest request) {

        //检查邮箱是否已存在
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new RuntimeException("邮箱已经被注册");
        }

        //创建新用户
        User user = new User();
        user.setUsername(request.getUserName());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());


        //保存用户
        if (userMapper.insert(user) != 1) {
            throw new RuntimeException("注册失败，请稍后重试");
        }


    }

    @Override
    public ApiResponse<Map> authenticate(String email, String password) {
    try {
        // 参数校验
        if (email == null || password == null) {
            return ApiResponse.error("邮箱和密码不能为空");
        }

        // 查询用户
        User user = userMapper.findByEmail(email);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        // 验证密码
        if (!password.equals(user.getPassword())) {
            log.warn("用户密码错误: {}", email);
            return ApiResponse.error("密码错误");
        }

        // 登录成功 - 生成 JWT Token
        User userByEmail = userMapper.findByEmail(email);
        String token = jwtUtil.generateToken(email);

        Map<String,Object> payload = new HashMap<>();
        payload.put("token", token);
        payload.put("user", userByEmail);

        return ApiResponse.success(payload);

    } catch (Exception e) {
        return ApiResponse.error("登录失败，请稍后重试");
    }
}

}