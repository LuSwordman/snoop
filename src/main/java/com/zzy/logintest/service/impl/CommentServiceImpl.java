package com.zzy.logintest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzy.logintest.domain.dto.CommentRequest;
import com.zzy.logintest.domain.dto.ReplyRequest;
import com.zzy.logintest.domain.pojo.Comment;
import com.zzy.logintest.domain.pojo.Post;
import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.ApiResponse;
import com.zzy.logintest.domain.vo.CommentVo;
import com.zzy.logintest.domain.vo.UserVo;
import com.zzy.logintest.mapper.CommentMapper;
import com.zzy.logintest.mapper.PostMapper;
import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.service.CommentService;
import com.zzy.logintest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostService postService;
    /**
     * 创建评论
     * @param postId
     * @param commentRequest
     */
    @Override
    public ResponseEntity<String> createParentComment(Long postId, CommentRequest commentRequest) {
        Comment  comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(commentRequest.getId());
        comment.setContent(commentRequest.getContent());
        commentMapper.insert(comment);
        //更新帖子评论数

        int result = postService.updateCommentPost(postId, 1);
        if (result == 0) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("评论成功");
    }

    @Override
    public ResponseEntity<List<CommentVo>> getComments(Long postId) {
        //1.1获取第一级评论
        List<Comment> commentList = getCommentsByPostId(postId);
        if (commentList.isEmpty()){
            return ResponseEntity.ok(new ArrayList<>());
        }
        //1.2转换成Map
        Map<Long, Comment> commentMap = commentList.stream().collect(Collectors.toMap(Comment::getId, comment -> comment));

        //2.1获取第二级评论
        List<Comment> childrenComments = getChildrenCommentsByParentIds(commentList.stream().map(Comment::getId).collect(Collectors.toList()));
        //2.2转换成Map
        Map<Long, List<Comment>> childrenCommentMap = childrenComments.stream().collect(Collectors.groupingBy(Comment::getParentId));

        //3.1获取全部用户id addAll -> 合并两个集合
        Set<Long> userIds = commentList.stream().map(Comment::getUserId).collect(Collectors.toSet());
        userIds.addAll(childrenComments.stream().map(Comment::getUserId).collect(Collectors.toSet()));

        //3.2获取用户信息
        List<User> userInfoByUserIds = getUserInfoByUserIds(userIds);

        //3.3转换成Map 同时适配userVo
        Map<Long, UserVo> userVoMap = userInfoByUserIds.stream().collect(Collectors.toMap(User::getId, user -> {
            UserVo userVo = new UserVo();
            userVo.setId(String.valueOf(user.getId())); //转换成字符串类型 防止bigint 类型在前端溢出
            userVo.setUserName(user.getUsername());
            userVo.setUserAvatar(user.getUserAvatar());
            return userVo;
        }));
        //4.1子评论用户信息填充
        Stream<CommentVo> childrenCommentsVoList = childrenComments.stream().map(comment -> {
            CommentVo commentVo = new CommentVo();
            commentVo.setId(String.valueOf(comment.getId()));
            commentVo.setPostId(String.valueOf(comment.getPostId()));
            commentVo.setContent(comment.getContent());
            commentVo.setParentId(String.valueOf(comment.getParentId()));
            commentVo.setCreatedAt(comment.getCreatedAt());
            commentVo.setUser(userVoMap.get(comment.getUserId()));
            return commentVo;
        });
        //4.2转换成Map
        Map<String, List<CommentVo>> childrenCommentsVoMap = childrenCommentsVoList.collect(Collectors.groupingBy(CommentVo::getParentId));


        List<CommentVo> list = commentList.stream().map(comment -> {
            CommentVo commentVo = new CommentVo();
            commentVo.setId(String.valueOf(comment.getId()));
            commentVo.setPostId(String.valueOf(comment.getPostId()));
            commentVo.setContent(comment.getContent());
            commentVo.setParentId(String.valueOf(comment.getParentId()));
            commentVo.setCreatedAt(comment.getCreatedAt());
            commentVo.setUser(userVoMap.get(comment.getUserId()));//获取用户信息对象
            commentVo.setChildren(childrenCommentsVoMap.getOrDefault(comment.getId().toString(), new ArrayList<>()));//获取子评论列表
            return commentVo;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    /**
     * 新增回复
     * @param replyRequest
     * @return
     */
    @Override
    public ResponseEntity<String> createReplyComment(ReplyRequest replyRequest) {
        Comment comment = new Comment();
        comment.setPostId(replyRequest.getPostId());
        comment.setUserId(replyRequest.getId());
        comment.setContent(replyRequest.getContent());
        comment.setParentId(replyRequest.getParentId());
        commentMapper.insert(comment);
        //增加评论数
        int result = postService.updateCommentPost(replyRequest.getPostId(), 1);
        if (result == 0) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("回复成功");
    }

    /**
     * 获取帖子下的第一级评论
     * @param postId
     * @return
     */
    private List<Comment> getCommentsByPostId(Long postId){
        //1查询评论列表
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        queryWrapper.isNull("parent_id"); //第一级
        return  commentMapper.selectList(queryWrapper);
    }

    /**
     * 获取评论区的第二季评论
     * @param parentIds
     */
    private List<Comment> getChildrenCommentsByParentIds(List<Long> parentIds){
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("parent_id", parentIds);
        return commentMapper.selectList(queryWrapper);
    }

    /**
     * 获取该帖子下的所有用户信息
     */
    private List<User> getUserInfoByUserIds(Set<Long> userIds){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", userIds);
        return userMapper.selectList(queryWrapper);
    }
}
