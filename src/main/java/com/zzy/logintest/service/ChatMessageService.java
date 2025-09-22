package com.zzy.logintest.service;

import com.zzy.logintest.domain.vo.ChatMessageVO;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

public interface ChatMessageService {
    void saveMessage(String sender, String receiver, String content, String type);

    /**
     * 分页查询两个用户之间的聊天记录
     * @param senderEmail 发送者邮箱
     * @param receiverEmail 接收者邮箱

     * @return 分页聊天记录响应
     */
    ResponseEntity<List<ChatMessageVO>> getMessages(String senderEmail, String receiverEmail);


}
