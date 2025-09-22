package com.zzy.logintest.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzy.logintest.common.Message;
import com.zzy.logintest.domain.dto.PostDto;
import com.zzy.logintest.domain.pojo.*;
import com.zzy.logintest.domain.vo.PostVo;
import com.zzy.logintest.mapper.*;

import com.zzy.logintest.service.PostService;
//import com.zzy.logintest.utils.MinioUploadUtil;

import com.zzy.logintest.utils.MinioUploadUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private MinioUploadUtil minioUploadUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private PostLikeMapper postLikeMapper;


    /**
     * 创建新帖子
     *
     * @param postDto
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<Post> createPost(PostDto postDto) {
        Post post = new Post();
        BeanUtils.copyProperties(postDto, post); //将前者相同字段值复制到后者对象
        postMapper.insert(post);
        // 创建图片 + 视频
        if (postDto.getImages() != null && postDto.getImages().length > 0) {
            for (int i = 0; i < postDto.getImages().length; i++) {
                PostImage postImage = new PostImage();
                postImage.setPostId(post.getId());
                // 上传图片并设置URL
                String imageUrl = minioUploadUtil.upload(postDto.getImages()[i]);
                postImage.setImageUrl(imageUrl);
                postImage.setSortOrder(i);
                postImagesMapper.insert(postImage);
            }
        }
        if (postDto.getVideo() != null) {
            PostVideo postVideo = new PostVideo();
            postVideo.setPostId(post.getId());
            // 上传视频并设置URL
            String videoUrl = minioUploadUtil.upload(postDto.getVideo());
            postVideo.setVideoUrl(videoUrl);
            postVideoMapper.insert(postVideo);
        }
        System.out.println("创建帖子成功");
        return ResponseEntity.ok(post);
    }

    /**
     * 根据id获取单个帖子详情
     *
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<PostVo> getPost(Long id, Long userId) {
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
        // 获取用户信息
        User user = userMapper.selectById(post.getUserId());
        if (user != null) {
            postVo.setUserAvatar(user.getUserAvatar());
            postVo.setUserName(user.getUsername());
        }

        if (post != null) {
            // 判断用户是否点赞
            QueryWrapper queryToLike = new QueryWrapper();
            queryToLike.eq("post_id", id);
            queryToLike.eq("user_id", userId);
            PostLike postLike = postLikeMapper.selectOne(queryToLike);
            if (postLike != null){
                postVo.setIsLike(true); //标记点赞
            }
            // 帖子浏览数+1
//            postMapper.incrementViewCount(id);
            // 吞吐量快10倍
            stringRedisTemplate.opsForValue().increment("post:view:" + post.getId(), 1);

            return ResponseEntity.ok(postVo);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 分页获取帖子列表
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseEntity<List<PostVo>> getPosts(int page, int size, Long userId) {
        //1获取偏移量
        int offset = (page - 1) * size;
        List<Post> posts = postMapper.findAll(offset, size);
        
        // 如果帖子列表为空，直接返回空结果
        if (posts.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        //2根据帖子表中的用户id 到用户表中获取用户名+头像 --优化写法 避免N+1次查询
        //根据帖子表中的id 到视频表中获取pojo
        Set<Long> userIds = posts.stream().map(Post::getUserId).collect(Collectors.toSet());
        Set<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toSet());

        //3根据帖子作者id批量查询用户  条件构造器 QueryWrapper
        List<User> users = new ArrayList<>();
        if( userIds != null && userIds.size() > 0){
            users = userMapper.selectBatchIds(new ArrayList<>(userIds));
        }

        //4根据帖子id批量查询视频
        List<PostVideo> postVideos = new ArrayList<>();
        if (postIds != null && postIds.size() > 0) {
            QueryWrapper<PostVideo> queryVideoWrapper = new QueryWrapper<>();
            queryVideoWrapper.in("post_id", postIds);
            postVideos = postVideoMapper.selectList(queryVideoWrapper); //获取帖子关联的视频
        }

        //5根据帖子id批量查询图片
        //根据图片优先级排序，获取优先级最高的一个图片 //数字越高 月优先级越大
        List<PostImage> postImages = new ArrayList<>();
        if (postIds != null && postIds.size() > 0) {
            postImages = postImagesMapper.selectTopImagesByPostIds(postIds);
        }

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
            if (postVideo != null) {
                postVo.setVideo(postVideo);
            }
            if (postImage != null) {
                List<PostImage> postImageList = List.of(postImage);
                postVo.setImages(postImageList);
            }
            //判断用户是否点赞
            QueryWrapper queryToLike = new QueryWrapper();
            queryToLike.eq("post_id", post.getId());
            queryToLike.eq("user_id", userId);
            PostLike postLike = postLikeMapper.selectOne(queryToLike);
            if (postLike != null){
                postVo.setIsLike(true); //标记点赞
            }
            postVos.add(postVo);
        }
        return ResponseEntity.ok(postVos);
    }

    /**
     * 更新帖子
     *
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
     *
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
     * 点赞赞帖子
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<String> updateLikePost(Long id, Long userId, int addCount) {
        String key = "post:like:" + id;
        //检查帖子是否存在 先打到缓存 缓存不存在则从数据库中查
        if (postMapper.findById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Message.POST_NOT_FOUND);
        }

        // 点赞
        if (addCount > 0) {
            Boolean hasLiked = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
            if (Boolean.TRUE.equals(hasLiked)) {
                // 已点过赞，避免重复操作
                return ResponseEntity.status(HttpStatus.OK).body(Message.ALREADY_LIKED);
            }
            //删除取消赞标记
            stringRedisTemplate.opsForSet().remove(key, "-" + userId);
            // 添加用户到点赞集合
            stringRedisTemplate.opsForSet().add(key, userId.toString());

            // 静默响应
            return ResponseEntity.ok().build();
        } else { // 取消赞
            // 检查用户是否点取消过赞 检查缓存
            Boolean hasLiked = stringRedisTemplate.opsForSet().isMember(key, "-1" + userId.toString()) ;

            if (hasLiked) {
                // 有取消赞标记
                return ResponseEntity.status(HttpStatus.OK).body(Message.ALREADY_DISLIKED);
            }
            // 删除点赞标记
            stringRedisTemplate.opsForSet().remove(key, userId.toString());
            // 添加取消赞标记
            stringRedisTemplate.opsForSet().add(key, "-" + userId);

            //静默响应
            return ResponseEntity.ok().build();
        }

    }
    @Override
    public int updateCommentPost(Long postId,Integer addCount) {
        return postMapper.updateCommentCount(postId,addCount);
    }

    /**
     * 模糊搜索帖子
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID
     * @return 搜索结果
     */
    @Override
    public ResponseEntity<List<PostVo>> searchPosts(String keyword, int page, int size, Long userId) {
        // 参数校验
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // 计算偏移量
        int offset = (page - 1) * size;

        // 搜索帖子
        List<Post> posts = postMapper.searchPosts(keyword.trim(), offset, size);

        if (posts.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        // 获取相关ID集合
        Set<Long> userIds = posts.stream().map(Post::getUserId).collect(Collectors.toSet());
        Set<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toSet());

        // 批量查询用户信息
        List<User> users = new ArrayList<>();
        if( userIds != null && userIds.size() > 0){
            users = userMapper.selectBatchIds(new ArrayList<>(userIds));
        }

        // 批量查询视频信息
        List<PostVideo> postVideos = new ArrayList<>();
        if (postIds != null && postIds.size() > 0) {
            QueryWrapper<PostVideo> queryVideoWrapper = new QueryWrapper<>();
            queryVideoWrapper.in("post_id", postIds);
            postVideos = postVideoMapper.selectList(queryVideoWrapper);
        }

        // 批量查询图片信息（获取优先级最高的图片）
        List<PostImage> postImages = new ArrayList<>();
        if (postIds != null && postIds.size() > 0) {
            postImages = postImagesMapper.selectTopImagesByPostIds(postIds);
        }

        // 批量查询点赞信息
        List<PostLike> postLikes = new ArrayList<>();
        if (postIds != null && postIds.size() > 0) {
            QueryWrapper<PostLike> queryLikeWrapper = new QueryWrapper<>();
            queryLikeWrapper.in("post_id", postIds);
            queryLikeWrapper.eq("user_id", userId);
            postLikes = postLikeMapper.selectList(queryLikeWrapper);
        }

        // 构建映射关系
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
        Map<Long, PostVideo> postVideoMap = postVideos.stream().collect(Collectors.toMap(PostVideo::getPostId, postVideo -> postVideo));
        Map<Long, PostImage> postImageMap = postImages.stream().collect(Collectors.toMap(PostImage::getPostId, postImage -> postImage));
        Set<Long> likedPostIds = postLikes.stream().map(PostLike::getPostId).collect(Collectors.toSet());

        // 构建返回结果
        List<PostVo> postVos = new ArrayList<>();
        for (Post post : posts) {
            PostVo postVo = new PostVo();
            BeanUtils.copyProperties(post, postVo);

            // 设置用户信息
            User user = userMap.get(post.getUserId());
            if (user != null) {
                postVo.setUserName(user.getUsername());
                postVo.setUserAvatar(user.getUserAvatar());
            }

            // 设置视频信息
            PostVideo postVideo = postVideoMap.get(post.getId());
            if (postVideo != null) {
                postVo.setVideo(postVideo);
            }

            // 设置图片信息
            PostImage postImage = postImageMap.get(post.getId());
            if (postImage != null) {
                List<PostImage> postImageList = List.of(postImage);
                postVo.setImages(postImageList);
            }

            // 设置点赞状态
            postVo.setIsLike(likedPostIds.contains(post.getId()));

            postVos.add(postVo);
        }

        return ResponseEntity.ok(postVos);
    }

}