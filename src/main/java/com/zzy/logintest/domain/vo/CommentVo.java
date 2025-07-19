package com.zzy.logintest.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentVo {
    private String id;
    private String postId;
    private String content;
    private String parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 评论者信息
    private UserVo user;
    //子评论--嵌套结构
    private List<CommentVo> children;
}