package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.PostImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface PostImagesMapper extends BaseMapper<PostImage> {
    //主页获取视频 现根据id列表 获取数据
    //再根据id分组 依优先级进行降序，获取第一条数据
    //数字越高 优先级越高
    List<PostImage> selectTopImagesByPostIds(Set<Long> postIds);
}
