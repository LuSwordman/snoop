package com.zzy.logintest.ws;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zzy.logintest.config.GetUserInfoConfigurator;

import com.zzy.logintest.domain.pojo.FriendRelation;

import com.zzy.logintest.mapper.FriendRelationMapper;
import com.zzy.logintest.service.ChatMessageService;
import com.zzy.logintest.utils.ChatMessageVo;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/ws/chat", configurator = GetUserInfoConfigurator.class)
public class ChatController {
    private static FriendRelationMapper friendRelationMapper;
    private static ChatMessageService chatMessageService;

    @Autowired
    public void setFriendRelationMapper(FriendRelationMapper mapper) {
        friendRelationMapper = mapper;
    }

    @Autowired
    public void setChatMessageService(ChatMessageService service) {
        chatMessageService = service;
    }

    @Getter
    public static final Map<String, Session> sessionPool = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        String email = (String) config.getUserProperties().get("email");
        if (email != null) {
            sessionPool.put(email, session);
            session.getUserProperties().put("email", email);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ChatMessageVo chatMessage = JSON.parseObject(message, ChatMessageVo.class);
        String sender = (String) session.getUserProperties().get("email");
        if (sender == null) return;
        if (chatMessage.getReceiver() != null && !chatMessage.getReceiver().isEmpty()) {
            sendToUser(sender, chatMessage.getReceiver(), chatMessage.getContent());
        }
    }

    @OnClose
    public void onClose(Session session) {
        String email = (String) session.getUserProperties().get("email");
        if (email != null) {
            sessionPool.remove(email);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket发生错误：" + throwable.getMessage());
        throwable.printStackTrace();
    }


    private void sendToUser(String senderEmail, String receiverEmail, String message) {
        Session receiverSession = sessionPool.get(receiverEmail);

        //建立互相关系
        FriendRelation friendRelation1 = new FriendRelation(senderEmail, receiverEmail);
        FriendRelation friendRelation2 = new FriendRelation(receiverEmail, senderEmail);
        QueryWrapper<FriendRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_email", senderEmail).eq("friend_id_email", receiverEmail);
        if(friendRelationMapper.selectOne(queryWrapper) == null) {
            friendRelationMapper.insert(friendRelation1);
            System.out.println("建立关系1成功");
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_email", receiverEmail).eq("friend_id_email", senderEmail);
        if(friendRelationMapper.selectOne(queryWrapper) == null) {
            friendRelationMapper.insert(friendRelation2);
            System.out.println("建立关系2成功");
        }

        ChatMessageVo chatMessageVo = new ChatMessageVo(ChatMessageVo.MessageType.USER, message, senderEmail, receiverEmail);
        if(receiverSession != null){
            receiverSession.getAsyncRemote().sendText(JSON.toJSONString(chatMessageVo));
        }
        chatMessageService.saveMessage(senderEmail, receiverEmail, message, "USER");

    }
}
