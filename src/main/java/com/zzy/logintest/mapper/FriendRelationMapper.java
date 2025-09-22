package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.FriendRelation;
import com.zzy.logintest.domain.vo.FriendRelationVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FriendRelationMapper extends BaseMapper<FriendRelation> {
}
