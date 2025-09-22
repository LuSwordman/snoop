package com.zzy.logintest.utils;

import lombok.Data;

@Data
public class ChatMessageVo {

    public enum MessageType {
        SYSTEM, // 系统消息
        USER    // 用户消息
    }

    private MessageType type;  // 消息类型
    private String content;    // 消息内容
    //接收者邮箱
    private String receiver;
    //发送者邮箱
    private String sender;

    public ChatMessageVo( MessageType type, String content,String sender,String receiver) {
        this.type = type;
        this.content = content;
        this.receiver = receiver;
        this.sender = sender;
    }


}
