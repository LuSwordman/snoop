package com.zzy.posts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.zzy.posts.domain.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper extends BaseMapper<User> {



}