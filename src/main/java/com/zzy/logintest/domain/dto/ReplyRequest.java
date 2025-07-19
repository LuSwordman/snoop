package com.zzy.logintest.domain.dto;

import lombok.Data;

@Data
public class ReplyRequest {
    private String content; //回复内容
    private Long id;        // 用户 ID
    private Long postId;    // 帖子 ID
    private Long parentId;  // 被回复的评论 ID（可为空表示一级
}
