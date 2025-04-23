package com.zzy.posts.domain.pojo;

import lombok.Data;

@Data
public class Post {
    private Long id;                    // 帖子ID
    private Long userId;                // 发布用户ID
    private String title;               // 帖子标题
    private String content;             // 帖子文字内容
    private Integer viewCount;          // 浏览次数
    private Integer likeCount;          // 点赞次数
    private Integer commentCount;       // 评论次数
    private String createdAt;           // 创建时间
    private String updatedAt;           // 更新时间
    private Integer status;             // 状态：1-正常，0-删除
}