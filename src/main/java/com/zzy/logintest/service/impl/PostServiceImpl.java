package com.zzy.logintest.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.pojo.Post;
import com.zzy.logintest.domain.pojo.PostImage;
import com.zzy.logintest.domain.pojo.PostVideo;
import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.PostVo;
import com.zzy.logintest.mapper.PostImagesMapper;
import com.zzy.logintest.mapper.PostMapper;

import com.zzy.logintest.mapper.PostVideoMapper;
import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.service.PostService;

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
    @Autowired
    private PostVideoMapper postVideoMapper;
    @Autowired
    private PostImagesMapper postImagesMapper;


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
    public ResponseEntity<PostVo> getPost(Long id) {
        //获取帖子实体
        Post post = postMapper.findById(id);
        //帖子实体转vo
        PostVo postVo = new PostVo();
        BeanUtils.copyProperties(post, postVo);
        //补充帖子媒体信息
        //1.1视频
        QueryWrapper<PostVideo> queryToVideo = new QueryWrapper<>();
        queryToVideo.eq("post_id", id);
        PostVideo postVideo = postVideoMapper.selectOne(queryToVideo);
        postVo.setVideo(postVideo);

        //1.2图片
        QueryWrapper<PostImage> queryToImages = new QueryWrapper<>();
        queryToImages.eq("post_id", id);
        List<PostImage> postImages = postImagesMapper.selectList(queryToImages);
        postVo.setImages(postImages);



        if (post != null) {
            postMapper.incrementViewCount(id);
            return ResponseEntity.ok(postVo);
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
        //2根据帖子表中的用户id 到用户表中获取用户名+头像 --优化写法 避免N+1次查询
        //根据帖子表中的id 到视频表中获取pojo
        Set<Long> userIds = posts.stream().map(Post::getUserId).collect(Collectors.toSet());
        Set<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toSet());

        //3根据帖子作者id批量查询用户  条件构造器 QueryWrapper
        List<User> users = userMapper.selectBatchIds(new ArrayList<>(userIds));

        //4根据帖子id批量查询视频
        QueryWrapper<PostVideo> queryVideoWrapper = new QueryWrapper<>();
        queryVideoWrapper.in("post_id", postIds);
        List<PostVideo> postVideos = postVideoMapper.selectList(queryVideoWrapper); //获取帖子关联的视频

        //5根据帖子id批量查询图片

        //根据图片优先级排序，获取优先级最高的一个图片 //数字越高 月优先级越大

        List<PostImage> postImages = postImagesMapper.selectTopImagesByPostIds(postIds);

        //5用户信息映射到map中
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        //6视频信息映射到map中
        Map<Long, PostVideo> postVideoMap = postVideos.stream().collect(Collectors.toMap(PostVideo::getPostId, postVideo -> postVideo));
        //7图片信息映射到map中
        Map<Long, PostImage> postImageMap = postImages.stream().collect(Collectors.toMap(PostImage::getPostId, postImage -> postImage));
        //7给每个帖子赋值对应的用户，图片，视频，信息
        List<PostVo> postVos = new ArrayList<>();
        for (Post post : posts) {
            PostVo postVo = new PostVo();
            BeanUtils.copyProperties(post, postVo);
            //具体信息不在主页展示
            postVo.setContent("");
            //此帖子作者信息
            User user = userMap.get(post.getUserId());
            //此帖子视频信息
            PostVideo postVideo = postVideoMap.get(post.getId());
            //此帖子图片信息
            PostImage postImage = postImageMap.get(post.getId());

            if (user != null) {
                postVo.setUserName(user.getUsername());
                postVo.setUserAvatar(user.getUserAvatar());
            }
            if(postVideo != null){
                postVo.setVideo(postVideo);
            }
            if(postImage != null){
                List<PostImage> postImageList = List.of(postImage);
                postVo.setImages(postImageList);
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
     * 取消赞帖子
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<Void> updateLikePost(Long id,int addCount) {
        //redis查询用户是否对该帖子点赞/未点赞
        //用户点赞
        System.out.println("取消赞帖子id:"+id);
        if (postMapper.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        if(postMapper.updateLikeCount(id, addCount) > 0){ //业务操作成功
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Override
    public int updateCommentPost(Long postId,Integer addCount) {
        return postMapper.updateCommentCount(postId,addCount);
    }

}