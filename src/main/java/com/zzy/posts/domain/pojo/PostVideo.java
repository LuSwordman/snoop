package com.zzy.posts.domain.pojo;

import lombok.Data;

@Data
public class PostVideo {
    private Long id;                    // 视频ID
    private Long postId;                // 关联的帖子ID
    private String videoUrl;            // 视频URL
    private String thumbnailUrl;        // 视频缩略图URL
    private Integer duration;           // 视频时长（秒）
    private Integer width;              // 视频宽度
    private Integer height;             // 视频高度
    private Integer sortOrder;          // 排序顺序
    private String createdAt;           // 创建时间
}