package com.zzy.logintest.service;

import com.zzy.logintest.domain.dto.RegisterRequest;
import com.zzy.logintest.domain.vo.ApiResponse;
import com.zzy.logintest.domain.vo.UserVo;

import java.util.List;



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

    /**
     * 根据用户名模糊搜索用户
     * @param username 用户名关键词
     * @param page 页码
     * @param size 每页大小
     * @return 用户列表
     */
    List<UserVo> searchUsers(String username, int page, int size);
}


