package com.zzy.logintest.domain.vo;

import lombok.Data;

@Data
public class UserVo {
    private String id;
    private String userName;
    private String userAvatar;
    //邮箱
    private String email;
}
