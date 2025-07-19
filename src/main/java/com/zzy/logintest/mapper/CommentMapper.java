package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
