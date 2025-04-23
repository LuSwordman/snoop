package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Insert("INSERT INTO users (username, email, password) " +
            "VALUES (#{username}, #{email}, #{password})")
    int insert(User user);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Update("UPDATE users SET password = #{password} WHERE email = #{email}")
    int updatePassword(@Param("email") String email, @Param("password") String password);

    /**
     * 根据id批量获取用户
     */



}