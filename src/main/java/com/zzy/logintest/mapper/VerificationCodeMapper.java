package com.zzy.logintest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zzy.logintest.domain.pojo.VerificationCode;
import org.apache.ibatis.annotations.*;


@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
    @Insert("INSERT INTO verification_code (email, code, expire_time, used) " +
            "VALUES (#{email}, #{code}, #{expireTime}, #{used})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VerificationCode code);

    @Select("SELECT * FROM verification_code " +
            "WHERE email = #{email} AND code = #{code} AND used = false " +
            "AND expire_time > NOW() " +
            "ORDER BY created_at DESC LIMIT 1")
    VerificationCode findValidCode(@Param("email") String email, @Param("code") String code);

    //设置已验证
    @Update("UPDATE verification_code SET used = true WHERE id = #{id}")
    int markAsUsed(Long id);
}