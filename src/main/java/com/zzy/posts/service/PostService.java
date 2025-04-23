package com.zzy.posts.service;



import com.zzy.posts.domain.dto.PostDto;
import com.zzy.posts.domain.pojo.Post;
import com.zzy.posts.domain.vo.PostVo;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface PostService {
    ResponseEntity<Post> createPost(PostDto postDro);

    ResponseEntity<Post> getPost(Long id);

    ResponseEntity<List<PostVo>> getPosts(int page, int size);

    ResponseEntity<Void> updatePost(Long id, Post post);

    ResponseEntity<Void> deletePost(Long id);

    ResponseEntity<Void> likePost(Long id);
}
