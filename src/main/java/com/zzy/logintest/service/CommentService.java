package com.zzy.logintest.service;

import com.zzy.logintest.domain.dto.CommentRequest;
import com.zzy.logintest.domain.dto.ReplyRequest;
import com.zzy.logintest.domain.vo.ApiResponse;
import com.zzy.logintest.domain.vo.CommentVo;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CommentService {
    ResponseEntity<String> createParentComment(Long postId, CommentRequest commentRequest);

    ResponseEntity<List<CommentVo>> getComments(Long postId);

    ResponseEntity<String> createReplyComment(ReplyRequest replyRequest);
}
