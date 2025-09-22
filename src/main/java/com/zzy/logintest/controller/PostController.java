package com.zzy.logintest.controller;

import com.zzy.logintest.Task.PostViewTask;
import com.zzy.logintest.domain.dto.CommentRequest;
import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.dto.ReplyRequest;
import com.zzy.logintest.domain.pojo.Post;
import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.*;

import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.service.CommentService;
import com.zzy.logintest.service.PostService;
import com.zzy.logintest.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostViewTask postViewTask;



    /**
     * 根据id获取帖子 + 播放量+1
     * redis 缓存 快10倍
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostVo> getPost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader)
    {
        if(authorizationHeader == null){
            ResponseEntity.badRequest().body("请先登录");
        }
        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 获取用户 email
        String userEmail = JwtUtil.getEmailFromToken(token);
        User user = userMapper.findByEmail(userEmail);
        return postService.getPost(id, user.getId());
    }

    /**
     * 分页获取帖子
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<List<PostVo>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authorizationHeader )
    {
        if(authorizationHeader == null){
            ResponseEntity.badRequest().body("请先登录");
        }
         // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 获取用户 email
        String userEmail = JwtUtil.getEmailFromToken(token);
        User user = userMapper.findByEmail(userEmail);

        return postService.getPosts(page, size,user.getId());

    }

    /**
     * 模糊搜索帖子
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @param authorizationHeader 授权头
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<List<PostVo>> searchPosts(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader == null) {
            return ResponseEntity.badRequest().build();
        }

        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 获取用户 email
        String userEmail = JwtUtil.getEmailFromToken(token);
        User user = userMapper.findByEmail(userEmail);

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        return postService.searchPosts(keyword, page, size, user.getId());
    }

    /**
     * 添加一级评论
     * @param postId
     * @param commentRequest
     * @return
     */
    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(@PathVariable Long postId, @RequestBody CommentRequest commentRequest) {
        return commentService.createParentComment(postId, commentRequest);
    }
    /**
     * 添加二级评论
     */
    @PostMapping("/comments/replies")
    public ResponseEntity<String> addReply(@RequestBody ReplyRequest replyRequest) {
        return commentService.createReplyComment(replyRequest);
    }

    /**
     * 分页获取一级评论列表
     * @param postId
     * @return
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<ParentCommentPageResponse> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentService.getParentComments(postId, page, size);
    }
    /**
     * 分页获取二级评论列表
     * @param parentCommentId
     * @return
     */
    @GetMapping("/comments/{parentCommentId}/replies")
    public ResponseEntity<ChildrenCommentPageResponse> getReplies(
            @PathVariable Long parentCommentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return commentService.getChildrenComments(parentCommentId, page, size);
    }

    /**
     * todo未实现前端 更新帖子
     * @param id
     * @param post
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    /**
     * todo 未实现前端 删除帖子
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }
    /**todo
     * 添加点赞/取消
     * redis 缓存
     * @param id
     * @return
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<String> addlikePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        if(authorizationHeader == null){
            ResponseEntity.badRequest().body("请先登录");
            return null;
        }
        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 获取用户 email
        String userEmail = JwtUtil.getEmailFromToken(token);
        User user = userMapper.findByEmail(userEmail);
        return postService.updateLikePost(id,user.getId(),1);
    }
    /**
     * 取消点赞
     * redis 缓存
     * @param id
     * @return
     */
    @DeleteMapping("/{id}/like")
    public ResponseEntity<String> deletelikePost(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorizationHeader) {
        if(authorizationHeader == null){
            ResponseEntity.badRequest().body("请先登录");
            return null;
        }
        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 获取用户 email
        String userEmail = JwtUtil.getEmailFromToken(token);
        User user = userMapper.findByEmail(userEmail);
        return postService.updateLikePost(id,user.getId(),-1);
    }

    /**
     * 创建帖子
     * @param
     * @return
     */
    @PostMapping("/create")
   public void createPost(
        @RequestParam("title") String title,
        @RequestParam("content") String content,
        @RequestParam(value = "images", required = false) MultipartFile[] images,
        @RequestParam(value = "sort_orders", required = false) List<Integer> sortOrders,
        @RequestParam(value = "video", required = false) MultipartFile video,
        @RequestHeader("Authorization") String authorizationHeader) {
        if(authorizationHeader == null){
            throw new RuntimeException("请先登录");
        }


        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 获取用户 email
        String userEmail = JwtUtil.getEmailFromToken(token);
        User user = userMapper.findByEmail(userEmail);

        // 创建 PostDto 对象
        PostDto postDto = new PostDto(user.getId(), title, content, images, video);
        System.out.println(images);

        // 处理其他逻辑
        postService.createPost(postDto);

    }

    /**
     * 手动触发浏览量同步（测试用）
     * @param postId 帖子ID
     * @return 同步结果
     */
    @PostMapping("/{postId}/sync-views")
    public ResponseEntity<String> syncPostViews(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader == null) {
            return ResponseEntity.badRequest().body("请先登录");
        }

        // 验证用户身份
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String userEmail = JwtUtil.getEmailFromToken(token);
            User user = userMapper.findByEmail(userEmail);

            if (user == null) {
                return ResponseEntity.badRequest().body("用户不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token无效");
        }

        // 获取Redis中的浏览量
        int redisViewCount = postViewTask.getRedisViewCount(postId);

        // 手动同步
        boolean success = postViewTask.syncSinglePostViewCount(postId);

        if (success) {
            return ResponseEntity.ok("同步成功，浏览量增加: " + redisViewCount);
        } else {
            return ResponseEntity.ok("没有需要同步的数据或同步失败");
        }
    }

    /**
     * 获取帖子Redis浏览量（测试用）
     * @param postId 帖子ID
     * @return Redis中的浏览量
     */
    @GetMapping("/{postId}/redis-views")
    public ResponseEntity<String> getRedisViews(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authorizationHeader) {

        if (authorizationHeader == null) {
            return ResponseEntity.badRequest().body("请先登录");
        }

        int redisViewCount = postViewTask.getRedisViewCount(postId);
        return ResponseEntity.ok("帖子ID: " + postId + " Redis浏览量: " + redisViewCount);
    }

}

