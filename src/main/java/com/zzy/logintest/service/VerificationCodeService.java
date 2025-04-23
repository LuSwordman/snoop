package com.zzy.logintest.service;

import com.zzy.logintest.domain.vo.ApiResponse;

public interface VerificationCodeService {
    /**
     * 发送验证码
     * @param email
     * @return
     */
    ApiResponse sendCode(String email);

    /**
     *重置密码
     * @param email
     * @param code
     * @param newPassword
     * @return
     */
    ApiResponse verifyCodeAndResetPassword(String email, String code, String newPassword);
}