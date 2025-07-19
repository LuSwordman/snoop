package com.zzy.logintest.domain.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;             // 评论ID
    private Long postId;         // 被评论的帖子ID
    private Long userId;         // 评论人ID
    private String content;      // 评论内容
    private Long parentId;       // 父评论ID（可为空）
    private LocalDateTime createdAt;   // 创建时间
    private LocalDateTime updatedAt;   // 更新时间
    private Boolean isDeleted;         // 是否逻辑删除


}
