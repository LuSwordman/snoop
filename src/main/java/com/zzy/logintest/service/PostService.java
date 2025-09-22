package com.zzy.logintest.service;



import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.pojo.Post;
import com.zzy.logintest.domain.vo.PostVo;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface PostService {
    ResponseEntity<Post> createPost(PostDto postDro);

    ResponseEntity<PostVo> getPost(Long id,Long userId);

    ResponseEntity<List<PostVo>> getPosts(int page, int size, Long userId);

    ResponseEntity<Void> updatePost(Long id, Post post);

    ResponseEntity<Void> deletePost(Long id);

    ResponseEntity<String> updateLikePost(Long id, Long userId, int addCount);

    int updateCommentPost(Long postId,Integer addCount);

    /**
     * 模糊搜索帖子
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID
     * @return 搜索结果
     */
    ResponseEntity<List<PostVo>> searchPosts(String keyword, int page, int size, Long userId);
}
