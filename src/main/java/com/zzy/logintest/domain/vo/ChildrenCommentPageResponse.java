package com.zzy.logintest.domain.vo;

import lombok.Data;

import java.util.List;
@Data
public class ChildrenCommentPageResponse {
    private long total;
    private List<ChildrenCommentVo> replies;

    public ChildrenCommentPageResponse(long total, List<ChildrenCommentVo> replies) {
        this.total = total;
        this.replies = replies;
    }

}
