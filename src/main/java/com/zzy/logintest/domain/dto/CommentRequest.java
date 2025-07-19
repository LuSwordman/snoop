package com.zzy.logintest.domain.dto;

import lombok.Data;

/**
 * 发表评论请求体
 */
@Data
public class CommentRequest {
    private Long id; //用户id
    private String content; //评论内容

}
