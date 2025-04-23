package com.zzy.posts.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PostDto {
    private Long userId;                // 发布用户ID
    private String title;               // 帖子标题
    private String content;             // 帖子文字内容
//    private MultipartFile[] images;     // 图片文件数组
//    private MultipartFile[] videos;     // 视频文件数组
}