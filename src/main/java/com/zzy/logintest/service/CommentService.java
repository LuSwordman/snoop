package com.zzy.logintest.service;

import com.zzy.logintest.domain.dto.CommentRequest;
import com.zzy.logintest.domain.dto.ReplyRequest;
import com.zzy.logintest.domain.vo.*;

import org.springframework.http.ResponseEntity;



public interface CommentService {
    ResponseEntity<String> createParentComment(Long postId, CommentRequest commentRequest);

    ResponseEntity<ParentCommentPageResponse> getParentComments(Long postId, int page, int size);

    ResponseEntity<ChildrenCommentPageResponse> getChildrenComments(Long postId, int page, int size);


    ResponseEntity<String> createReplyComment(ReplyRequest replyRequest);
}
