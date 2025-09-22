package com.zzy.logintest.controller;

import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.domain.vo.UserVo;
import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.service.UserService;
import com.zzy.logintest.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户相关操作控制器
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据用户名模糊搜索用户
     * @param username 用户名关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserVo>> searchUsers(
            @RequestParam("username") String username,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
         ) {

        // 执行搜索
        List<UserVo> searchResults = userService.searchUsers(username, page, size);
        return ResponseEntity.ok(searchResults);
    }
}
