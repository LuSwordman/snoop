package com.zzy.logintest.filter;

import com.zzy.logintest.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    String path = request.getRequestURI();
    System.out.println("JwtAuthenticationFilter - Processing request for path: " + path);

    // 跳过登录、注册等不需要认证的接口
    if (path.equals("/api/login") || path.equals("/api/register")) {
        filterChain.doFilter(request, response);
        return;
    }

    String authHeader = request.getHeader("Authorization");
    System.out.println("JwtAuthenticationFilter - Authorization header: " + authHeader);

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        System.out.println("JwtAuthenticationFilter - Extracted token: " + token);

        try {
            if (jwtUtil.validateToken(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                System.out.println("JwtAuthenticationFilter - Token validated for email: " + email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("JwtAuthenticationFilter - Authentication set in SecurityContext");
            } else {
                System.out.println("JwtAuthenticationFilter - Token validation failed");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception e) {
            System.out.println("JwtAuthenticationFilter - Error during token validation: " + e.getMessage());
            logger.error("JWT认证失败", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
    } else {
        System.out.println("JwtAuthenticationFilter - No valid Authorization header found");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    filterChain.doFilter(request, response);
}

} 