package com.zzy.logintest.service.impl;


import com.zzy.logintest.domain.pojo.ChatMessage;
import com.zzy.logintest.domain.vo.ChatMessageVO;
import com.zzy.logintest.mapper.ChatMessageMapper;
import com.zzy.logintest.service.ChatMessageService;
import org.bouncycastle.util.Arrays;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;


    @Override
    public void saveMessage(String sender, String receiver, String content, String type) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setContent(content);
        chatMessage.setType(type);
        chatMessageMapper.insert(chatMessage);
    }

    @Override
    public ResponseEntity<List<ChatMessageVO>> getMessages(String senderEmail, String receiverEmail) {
        // 如果没有指定before时间，则使用当前时间
//        Instant queryBefore = before != null ? before : Instant.now();

        // 查询消息记录
        List<ChatMessage> messages = chatMessageMapper.findAllMessages(
            senderEmail,
            receiverEmail
        );

        // 转换为VO对象
        List<ChatMessageVO> messageVOs = messages.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(messageVOs);
    }

    private ChatMessageVO convertToVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        vo.setId(message.getId());
        vo.setContent(message.getContent());
        vo.setSender(message.getSender());
        vo.setReceiver(message.getReceiver());
        vo.setTimestamp(message.getTimestamp());
        vo.setType(message.getType());
        return vo;
    }
}
