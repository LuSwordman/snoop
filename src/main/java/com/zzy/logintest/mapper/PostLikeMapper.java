package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.PostLike;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {

    @Select("SELECT * FROM post_like WHERE post_id = #{id} AND user_id = #{userId}")
    PostLike findByPostIdAndUserId(Long id, Long userId);


    int batchInsert(List<PostLike> postLikes);

}
