package com.zzy.logintest.domain.vo;

import com.zzy.logintest.domain.pojo.ChatMessage;
import com.zzy.logintest.utils.ChatMessageVo;
import lombok.Data;


@Data
public class FriendRelationVO {
   private String id;
    private String userName;
    private String userAvatar;
    //邮箱
    private String email;
    //在线
    private Boolean online;
}
