package com.zzy.logintest.controller;

import com.zzy.logintest.domain.dto.EmailRequest;
import com.zzy.logintest.domain.dto.ResetPasswordRequest;
import com.zzy.logintest.domain.vo.ApiResponse;
import com.zzy.logintest.service.VerificationCodeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 发送验证码和重置密码
 */
@RestController
@CrossOrigin
@Slf4j
public class EmailController {
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public EmailController(VerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    @PostMapping("/send-verification-code")
    public ApiResponse<?> sendVerificationCode(@Valid @RequestBody EmailRequest request) {

        try {
            System.out.println("发送到："+ request.toString());
            ApiResponse response = verificationCodeService.sendCode(request.getEmail());
            return response;
        } catch (Exception e) {
            return ApiResponse.error("发送验证码失败");
        }
    }

    @PostMapping("/reset-password")
    public ApiResponse<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {

            ApiResponse response = verificationCodeService.verifyCodeAndResetPassword(
                request.getEmail(),
                request.getCode(),
                request.getNewPassword()
            );
            return response;
        } catch (Exception e) {
            return ApiResponse.error("重置密码失败");
        }
    }
}