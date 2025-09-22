package com.zzy.logintest.domain.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostLike {
    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;

    public PostLike(Long postId, Long userId) {
        this.postId = postId;
        this.userId = userId;
    }
}
