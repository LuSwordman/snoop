package com.zzy.logintest.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ParentCommentPageResponse {
    private long total;
    private List<ParentCommentVo> comments;

    public ParentCommentPageResponse(long total, List<ParentCommentVo> replies) {
        this.total = total;
        this.comments = replies;
    }


}
