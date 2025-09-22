package com.zzy.logintest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzy.logintest.domain.pojo.FriendRelation;
import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.FriendRelationVO;

import com.zzy.logintest.mapper.FriendRelationMapper;
import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.service.FriendRelationService;
import com.zzy.logintest.ws.ChatController;
import jakarta.websocket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;



@Service
public class FriendRelationImpl implements FriendRelationService {
    @Autowired
    private FriendRelationMapper friendRelationMapper;
    @Autowired
    private UserMapper userMapper;

    private final Map<String, Session> sessionPool = ChatController.getSessionPool();

    /**
     * 获取好友关系
     * @param userEmail
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseEntity<List<FriendRelationVO>> getFriendRelation(String userEmail, int page, int size) {
        Page<FriendRelation> page1 = new Page<>(page, size);
        QueryWrapper<FriendRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_email", userEmail);
        friendRelationMapper.selectPage(page1, queryWrapper);
        List<FriendRelation> friendRelations = page1.getRecords();
        List<String> friendEmails = friendRelations.stream().map(c -> c.getFriendIdEmail()).collect(Collectors.toList());

       // 3. 批量查询 User 表
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("email", friendEmails);
        if (friendEmails.isEmpty()){
            return ResponseEntity.ok(null);
        }
        List<User> users = userMapper.selectList(userQuery);


        //获取socket session

        //user --> friendRelationVO
        List<FriendRelationVO> userVos = users.stream().map(user -> {
            FriendRelationVO friendRelationVO = new FriendRelationVO();
            friendRelationVO.setUserName(user.getUsername());
            friendRelationVO.setUserAvatar(user.getUserAvatar());
            friendRelationVO.setEmail(user.getEmail());
            //判断是否线
            if(sessionPool.containsKey(user.getEmail())){
                friendRelationVO.setOnline(true);
            }
            return friendRelationVO;
        }).toList();
        return ResponseEntity.ok(userVos);

    }
}
