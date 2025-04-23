package com.zzy.logintest.service;

import com.zzy.logintest.domain.dto.RegisterRequest;

import com.zzy.logintest.domain.vo.ApiResponse;



public interface UserService {
    /**
     * 用户认证
     * @param email
     * @param password
     */
    ApiResponse authenticate(String email, String password);

    /**
     * 注册新用户
     * @param request
     */
    void register(RegisterRequest request);
}


