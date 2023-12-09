package com.example.Interceptor;

import com.example.entity.user.AccountUser;
import com.example.mapper.UserMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 拦截器
@Component // 注册为bean
public class AuthorizeInterceptor implements HandlerInterceptor {

    @Resource
    UserMapper userMapper;

    // 处理前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        AccountUser account = userMapper.findAccountUserByNameOrEmail(username);
        request.getSession().setAttribute("account", account); // Controller层可以拿到数据
        return true;
    }
}
