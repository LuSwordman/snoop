package com.zzy.logintest.controller;


import com.zzy.logintest.domain.vo.ChatMessageVO;
import com.zzy.logintest.service.ChatMessageService;
import com.zzy.logintest.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * 聊天消息相关接口
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class MessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private JwtUtil jwtUtil;


    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessageVO>> getMessages(
            @RequestParam String senderEmail,
            @RequestParam String receiverEmail,
            @RequestParam(required = false, defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before) {


        // 调用service获取分页聊天记录
        return chatMessageService.getMessages(senderEmail, receiverEmail);
    }



}
