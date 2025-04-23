package com.zzy.posts.domain.pojo;

import lombok.Data;

@Data
public class PostImage {
    private Long id;                    // 图片ID
    private Long postId;                // 关联的帖子ID
    private String imageUrl;            // 图片URL
    private Integer width;              // 图片宽度
    private Integer height;             // 图片高度
    private Integer sortOrder;          // 排序顺序
    private String createdAt;           // 创建时间
}