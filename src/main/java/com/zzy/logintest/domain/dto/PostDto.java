package com.zzy.logintest.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostDto {
    private Long userId;                // 发布用户ID
    private String title;               // 帖子标题
    private String content;             // 帖子文字内容
    private MultipartFile[] images;     // 图片文件数组
    private MultipartFile video;     // 视频文件

    public PostDto(Long userId, String title, String content, MultipartFile[] images, MultipartFile video) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.images = images;
        this.video = video;
    }
}