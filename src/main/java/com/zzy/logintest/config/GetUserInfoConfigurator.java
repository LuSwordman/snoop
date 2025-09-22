package com.zzy.logintest.config;

import com.zzy.logintest.domain.pojo.User;
import com.zzy.logintest.utils.JwtUtil;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * 从http请求获取token,根据token存储用户邮箱到websocket
 */
@Configuration
public class GetUserInfoConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map<String, List<String>> params = request.getParameterMap();
        List<String> tokenList = params.get("token");

        if (tokenList == null || tokenList.isEmpty()) {
            return;
        }

        String token = tokenList.get(0);
        String userEmail = JwtUtil.getEmailFromToken(token);
        if (userEmail == null || userEmail.isEmpty()) {
            return;
        }

        sec.getUserProperties().put("email", userEmail);
    }


}
