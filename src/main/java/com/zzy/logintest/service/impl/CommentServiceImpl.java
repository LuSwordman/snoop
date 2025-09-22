package com.zzy.logintest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.logintest.domain.dto.CommentRequest;
import com.zzy.logintest.domain.dto.ReplyRequest;
import com.zzy.logintest.domain.pojo.Comment;
import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.*;

import com.zzy.logintest.mapper.CommentMapper;
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

    //分页获取一级评论
    @Override
    public ResponseEntity<ParentCommentPageResponse> getParentComments(Long postId, int page, int size) {
        //1.1获取第一级评论
        List<Comment> commentList = getParentCommentsByPostId(postId, page, size);
        if (commentList.isEmpty()){
            return ResponseEntity.ok(new ParentCommentPageResponse(0, new ArrayList<>()));
        }

        //2.2转换成Map
        Map<Long, Comment> commentMap = commentList.stream().collect(Collectors.toMap(Comment::getId, comment -> comment));


        //3.1获取全部用户id
        Set<Long> userIds = commentList.stream().map(Comment::getUserId).collect(Collectors.toSet());

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

        List<ParentCommentVo> list = commentList.stream().map(comment -> {
            ParentCommentVo parentCommentVo = new ParentCommentVo();
            parentCommentVo.setId(String.valueOf(comment.getId()));
            parentCommentVo.setPostId(String.valueOf(comment.getPostId()));
            parentCommentVo.setContent(comment.getContent());
            parentCommentVo.setParentId(String.valueOf(comment.getParentId()));
            parentCommentVo.setCreatedAt(comment.getCreatedAt());
            parentCommentVo.setUser(userVoMap.get(comment.getUserId()));//获取用户信息对象
            return parentCommentVo;
        }).collect(Collectors.toList());

        /**
         * 获取总数todo
         */
        Long total = commentMapper.selectCount(new QueryWrapper<Comment>().eq("post_id", postId));

        return ResponseEntity.ok(new ParentCommentPageResponse(total, list));
    }

    //分页获取二级评论
    @Override
    public ResponseEntity<ChildrenCommentPageResponse> getChildrenComments(Long parentCommentId, int page, int size) {
        //1.1获取第二级评论
        List<Comment> commentList = getChildrenCommentsByParentIds(parentCommentId, page, size);
        if (commentList.isEmpty()){
            return ResponseEntity.ok(new ChildrenCommentPageResponse(0, new ArrayList<>()));
        }

        //2.2转换成Map
        Map<Long, Comment> commentMap = commentList.stream().collect(Collectors.toMap(Comment::getId, comment -> comment));


        //3.1获取全部用户id
        Set<Long> userIds = commentList.stream().map(Comment::getUserId).collect(Collectors.toSet());

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

        List<ChildrenCommentVo> list = commentList.stream().map(comment -> {
            ChildrenCommentVo childrenCommentVo = new ChildrenCommentVo();
            childrenCommentVo.setId(String.valueOf(comment.getId()));
            childrenCommentVo.setPostId(String.valueOf(comment.getPostId()));
            childrenCommentVo.setContent(comment.getContent());
            childrenCommentVo.setParentId(String.valueOf(comment.getParentId()));
            childrenCommentVo.setCreatedAt(comment.getCreatedAt());
            childrenCommentVo.setUser(userVoMap.get(comment.getUserId()));//获取用户信息对象
            return childrenCommentVo;
        }).collect(Collectors.toList());
        /**
         * 获取总数todo
         */
        Long total = commentMapper.selectCount(new QueryWrapper<Comment>().eq("parent_id", parentCommentId));

        return ResponseEntity.ok(new ChildrenCommentPageResponse(total, list));
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
     * 分页获取帖子下的第一级评论
     * @param postId
     * @return
     */
    private List<Comment> getParentCommentsByPostId(Long postId, int page, int size){
        Page<Comment> pageRequest = new Page<>(page, size);
        //1查询评论列表
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId);
        queryWrapper.isNull("parent_id"); //第一级
        queryWrapper.orderByDesc("created_at"); //创建时间倒序
        Page<Comment> commentPage = commentMapper.selectPage(pageRequest, queryWrapper);
        return  commentPage.getRecords();
    }

    /**
     * 分页获取评论区的第二级评论
     * @param parentId
     */
    private List<Comment> getChildrenCommentsByParentIds(Long parentId, int page, int size){
        //创建Page对象
        Page<Comment> pageRequest = new Page<>(page, size);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        queryWrapper.orderByDesc("created_at"); //倒序创建
        Page<Comment> commentPage = commentMapper.selectPage(pageRequest, queryWrapper);
        return commentPage.getRecords();
    }

    /**
     * 批量获取用户信息
     */
    private List<User> getUserInfoByUserIds(Set<Long> userIds){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", userIds);
        return userMapper.selectList(queryWrapper);
    }
}
