package com.zzy.posts.controller;

import com.zzy.posts.domain.dto.PostDto;
import com.zzy.posts.domain.pojo.Post;
import com.zzy.posts.domain.vo.PostVo;
import com.zzy.posts.service.PostService;
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
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
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

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long id) {
        return postService.likePost(id);
    }
}

