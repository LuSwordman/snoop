package com.zzy.logintest.controller;


import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.FriendRelationVO;
import com.zzy.logintest.domain.vo.UserVo;
import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.service.FriendRelationService;
import com.zzy.logintest.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 */
@CrossOrigin
@RestController
@RequestMapping("/api/friends")
public class DataChatController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FriendRelationService friendRelationService;

    /**
     * 获取好友列表 (是否在线
     * @param page
     * @param size
     * @param authorizationHeader
     * @return
     */
    @GetMapping
    public  ResponseEntity<List<FriendRelationVO>>  getFriendRelationData(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authorizationHeader){
        //请求体获取用户邮箱
        if (authorizationHeader == null) {
            return ResponseEntity.badRequest().build();
        }

        // 从 Authorization 头中提取 token
        String token = authorizationHeader.replace("Bearer ", "");

        // 获取用户 email
        String userEmail = JwtUtil.getEmailFromToken(token);

        return friendRelationService.getFriendRelation(userEmail, page, size);
    }

}
