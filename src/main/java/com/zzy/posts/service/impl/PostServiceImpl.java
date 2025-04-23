package com.zzy.posts.service.impl;


import com.zzy.posts.domain.dto.PostDto;
import com.zzy.posts.domain.pojo.Post;
import com.zzy.posts.domain.pojo.User;
import com.zzy.posts.domain.vo.PostVo;
import com.zzy.posts.mapper.PostMapper;

import com.zzy.posts.mapper.UserMapper;
import com.zzy.posts.service.PostService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;


    /**
     * 创建新帖子
     * @param postDto
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<Post> createPost(PostDto postDto) {
        Post post = new Post();
        BeanUtils.copyProperties(postDto, post); //将前者相同字段值复制到后者对象
        postMapper.insert(postDto);
        return ResponseEntity.ok(post);
    }

    /**
     * 根据id获取单个帖子详情
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<Post> getPost(Long id) {
        Post post = postMapper.findById(id);
        if (post != null) {
            postMapper.incrementViewCount(id);
            return ResponseEntity.ok(post);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 分页获取帖子列表
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseEntity<List<PostVo>> getPosts(int page, int size) {
        //1获取偏移量
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findAll(offset, size);
        //2更具帖子表中的用户id 到用户表中获取用户名+头像 --优化写法 避免N+1次查询
        Set<Long> userIds = posts.stream().map(Post::getUserId).collect(Collectors.toSet());
        //3批量查询用户
        List<User> users = userMapper.selectBatchIds(new ArrayList<>(userIds));
        //4映射到map中
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        // 5. 给每个帖子赋值对应的用户信息
        List<PostVo> postVos = new ArrayList<>();
        for (Post post : posts) {
            PostVo postVo = new PostVo();
            BeanUtils.copyProperties(post, postVo);
            User user = userMap.get(post.getUserId());
            if (user != null) {
                postVo.setUserName(user.getUsername());
                postVo.setUserAvatar(user.getUserAvatar());
            }

            postVos.add(postVo);
        }
        return ResponseEntity.ok(postVos);
    }

    /**
     * 更新帖子
     * @param id
     * @param post
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<Void> updatePost(Long id, Post post) {
        if (postMapper.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        post.setId(id);
        return postMapper.update(post) > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * 删除帖子
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<Void> deletePost(Long id) {
        if (postMapper.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return postMapper.delete(id) > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * 点赞帖子
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<Void> likePost(Long id) {
        //用户点赞
        System.out.println("点赞帖子id:"+id);
        if (postMapper.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return postMapper.incrementLikeCount(id) > 0 ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

}