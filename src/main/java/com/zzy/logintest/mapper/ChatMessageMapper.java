package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.Instant;
import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {



    @Select("SELECT * FROM chat_message " +
        "WHERE ((sender = #{senderEmail} AND receiver = #{receiverEmail}) " +
        "   OR (sender = #{receiverEmail} AND receiver = #{senderEmail})) " +
        "AND timestamp < #{before} " +
        "ORDER BY timestamp DESC " +
        "LIMIT #{size}")
    List<ChatMessage> findMessagesBetweenUsers(
        @Param("senderEmail") String senderEmail,
        @Param("receiverEmail") String receiverEmail,
        @Param("before") Instant before,
        @Param("size") int size);
       /**
     * 获取全部聊天记录
     * @param senderEmail
     * @param receiverEmail
     * @return
     */
    @Select("SELECT * FROM chat_message " +
        "WHERE ((sender = #{senderEmail} AND receiver = #{receiverEmail}) " +
        "   OR (sender = #{receiverEmail} AND receiver = #{senderEmail})) ")
    List<ChatMessage> findAllMessages(
        @Param("senderEmail") String senderEmail,
        @Param("receiverEmail") String receiverEmail);
}