package com.zzy.logintest.domain.pojo;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class ChatMessage{
    private Long id;
    private String sender;
    private String receiver;
    private String content;
    private String type;
    private Instant timestamp;
}
