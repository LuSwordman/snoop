package com.zzy.logintest.controller;

import com.zzy.logintest.domain.dto.LoginRequest;
import com.zzy.logintest.domain.dto.RegisterRequest;
import com.zzy.logintest.domain.vo.ApiResponse;
import com.zzy.logintest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录和注册
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest request) {
        System.out.println(request.toString());

        return  userService.authenticate(request.getEmail(), request.getPassword());
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "注册成功");
        return ResponseEntity.ok(response);
    }
}