package com.zzy.logintest.service.impl;

import com.zzy.logintest.domain.pojo.VerificationCode;
import com.zzy.logintest.domain.vo.ApiResponse;

import com.zzy.logintest.mapper.UserMapper;
import com.zzy.logintest.mapper.VerificationCodeMapper;
import com.zzy.logintest.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    //mapper
    private final VerificationCodeMapper verificationCodeMapper;
    private final UserMapper userMapper;

    //发送器
     private final JavaMailSender mailSender;

    @Autowired
    public VerificationCodeServiceImpl(VerificationCodeMapper verificationCodeMapper,
                                     JavaMailSender mailSender,
                                     UserMapper userMapper) {
        this.verificationCodeMapper = verificationCodeMapper;
        this.mailSender = mailSender;
        this.userMapper = userMapper;
    }

    @Override
    public ApiResponse sendCode(String email) {
        try {

            if (userMapper.findByEmail(email) == null) {
                return ApiResponse.error("该邮箱未注册");
            }

            //生成
            String code = generateVerificationCode();

            //保存
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
            verificationCode.setCode(code);
            verificationCode.setExpireTime(LocalDateTime.now().plusMinutes(1));
            verificationCode.setUsed(false);

            verificationCodeMapper.insert(verificationCode);

//            System.out.println("验证码：" + code);
//            System.out.println(verificationCode.toString());

            // 3. 发送邮件
            sendVerificationEmail(email, code);

            return ApiResponse.success("验证码已发送");
        } catch (Exception e) {
            return ApiResponse.error("发送验证码失败");
        }
    }

    @Override
    public ApiResponse verifyCodeAndResetPassword(String email, String code, String newPassword) {
        try {
            //验证验证码
            System.out.println("---------" + code);
            VerificationCode verificationCode = verificationCodeMapper.findValidCode(email, code);
            if (verificationCode == null) {
                return ApiResponse.error("验证码无效或已过期");
            }

            // 2. 更新密码
            int updated = userMapper.updatePassword(email, newPassword);
            if (updated == 0) {
                return ApiResponse.error("密码重置失败");
            }

            // 3. 标记验证码已使用
            verificationCodeMapper.markAsUsed(verificationCode.getId());

            return ApiResponse.success("密码重置成功");
        } catch (Exception e) {
            log.error("密码重置失败: {}", e.getMessage());
            return ApiResponse.error("密码重置失败");
        }
    }

    // 生成6位随机验证码
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // 发送验证码邮件
    private void sendVerificationEmail(String toEmail, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2096486729@qq.com");
        message.setTo(toEmail);

        //日期转换
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = localDateTime.format(formatter);


        System.out.println("发送邮件到：" + toEmail + "---------------");

        message.setSubject("密码重置验证码");
        message.setText(
                        "验证码是: " + code +
                        "\n验证码有效期为1分钟" +
                        "\n请在"+ time + "之前完成验证"
                     );

        mailSender.send(message);
    }
}