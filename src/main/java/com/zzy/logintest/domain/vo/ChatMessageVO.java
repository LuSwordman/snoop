package com.zzy.logintest.domain.vo;

import lombok.Data;

import java.time.Instant;


@Data
public class ChatMessageVO {

    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private String type;
    private Instant timestamp; // 前端需要的时间戳格式（毫秒）

    public ChatMessageVO() {}

    public ChatMessageVO(Long id, String sender, String receiver, String content, String type, Instant timestamp) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.type = type;
        this.timestamp = timestamp;
    }

}
