package com.zzy.logintest.domain.vo;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    // 构造器和静态工厂方法
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "操作成功", data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message, null);
    }


}