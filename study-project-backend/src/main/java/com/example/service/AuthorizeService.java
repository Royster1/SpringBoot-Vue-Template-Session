package com.example.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    // 发送邮件
    boolean sendValidateEmail(String email, String sessionId); // 把sessionId也作为发送邮件凭据, 防止换个邮箱就绕过60s发送
}
