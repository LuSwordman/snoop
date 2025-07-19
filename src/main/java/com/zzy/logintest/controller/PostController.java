package com.zzy.logintest.controller;

import com.zzy.logintest.domain.dto.CommentRequest;
import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.dto.ReplyRequest;
import com.zzy.logintest.domain.pojo.Post;
import com.zzy.logintest.domain.vo.CommentVo;
import com.zzy.logintest.domain.vo.PostVo;
import com.zzy.logintest.service.CommentService;
import com.zzy.logintest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody PostDto postDto) {
        return postService.createPost(postDto);
    }

    /**
     * 根据获取帖子（view+1）
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostVo> getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseEntity<List<PostVo>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return postService.getPosts(page, size);

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
    //添加二级评论
    @PostMapping("/comments/replies")
    public ResponseEntity<String> addReply(@RequestBody ReplyRequest replyRequest) {
        return commentService.createReplyComment(replyRequest);
    }



    /**
     * 获取评论列表
     * @param postId
     * @return
     */
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentVo>> getComments(@PathVariable Long postId) {
        return commentService.getComments(postId);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> addlikePost(@PathVariable Long id) {
        return postService.updateLikePost(id,1);
    }
    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> deletelikePost(@PathVariable Long id) {
        return postService.updateLikePost(id,-1);
    }
}

