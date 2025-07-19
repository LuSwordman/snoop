package com.zzy.logintest.service;



import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.pojo.Post;
import com.zzy.logintest.domain.vo.PostVo;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface PostService {
    ResponseEntity<Post> createPost(PostDto postDro);

    ResponseEntity<PostVo> getPost(Long id);

    ResponseEntity<List<PostVo>> getPosts(int page, int size);

    ResponseEntity<Void> updatePost(Long id, Post post);

    ResponseEntity<Void> deletePost(Long id);

    ResponseEntity<Void> updateLikePost(Long id, int addCount);

    int updateCommentPost(Long postId,Integer addCount);
}
