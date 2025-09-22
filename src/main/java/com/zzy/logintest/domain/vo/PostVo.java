package com.zzy.logintest.domain.vo;

import com.zzy.logintest.domain.pojo.PostImage;
import com.zzy.logintest.domain.pojo.PostVideo;
import lombok.Data;
import java.util.List;

@Data
public class PostVo {
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
    //用户名+头像
    private String userName;
    private String userAvatar;
    
    // 关联的图片列表
    private List<PostImage> images;
    // 关联的视频列表
    private PostVideo video;

    //是否点赞
    private Boolean isLike;
}