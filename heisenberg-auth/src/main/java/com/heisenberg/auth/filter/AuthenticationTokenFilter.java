package com.heisenberg.auth.filter;


import cn.hutool.db.nosql.redis.RedisDS;
import com.heisenberg.auth.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("authentication");
        if (!StringUtils.hasText(token)) {
            //token为空的话, 就不管它, 让SpringSecurity中的其他过滤器处理请求
            //请求放行
            filterChain.doFilter(request, response);
        }

        int userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            //解析出userid
            userid = claims.get("userId", Integer.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("token非法");
        }
    }
}
