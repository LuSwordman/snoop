package com.zzy.logintest.domain.pojo;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class VerificationCode {
    private Long id;
    private String email;
    private String code;
    private LocalDateTime expireTime;
    private Boolean used;
}