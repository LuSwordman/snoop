package com.zzy.logintest.domain.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendRelation {
    private Long id;
    private String userEmail;
    private String friendIdEmail;
    private Integer status; // 0=未确认，1=已确认，2=拉黑
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public FriendRelation(String userEmail, String friendIdEmail) {
        this.userEmail = userEmail;
        this.friendIdEmail = friendIdEmail;
        this.status = 1;
    }

}
