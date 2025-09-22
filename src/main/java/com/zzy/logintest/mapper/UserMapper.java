package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Insert("INSERT INTO user (username, email, password) " +
            "VALUES (#{username}, #{email}, #{password})")
    int insert(User user);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    @Update("UPDATE user SET password = #{password} WHERE email = #{email}")
    int updatePassword(@Param("email") String email, @Param("password") String password);

    /**
     * 根据id批量获取用户
     */

    /**
     * 根据用户名模糊搜索用户
     * @param username 用户名关键词
     * @param offset 偏移量
     * @param size 每页大小
     * @return 用户列表
     */
    @Select("SELECT id, username, email, created_at, updated_at, user_avatar FROM user WHERE username LIKE CONCAT('%', #{username}, '%') ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<User> searchUsersByUsername(@Param("username") String username, @Param("offset") int offset, @Param("size") int size);

    /**
     * 获取用户名搜索结果总数
     * @param username 用户名关键词
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM user WHERE username LIKE CONCAT('%', #{username}, '%')")
    int countSearchUserResults(@Param("username") String username);

}