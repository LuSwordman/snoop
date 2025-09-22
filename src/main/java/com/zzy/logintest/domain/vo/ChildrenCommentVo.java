package com.zzy.logintest.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChildrenCommentVo {
    private String id;
    private String postId;
    private String content;
    private String parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 评论者信息
    private UserVo user;

}