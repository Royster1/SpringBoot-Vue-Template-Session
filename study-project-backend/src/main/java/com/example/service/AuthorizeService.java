package com.example.service;

import com.example.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthorizeService extends UserDetailsService {

    // 发送邮件
    String sendValidateEmail(String email, String sessionId, boolean hasAccount); // 把sessionId也作为发送邮件凭据, 防止换个邮箱就绕过60s发送

    // 验证并注册
    String validateAndRegister(String username, String password, String email, String code, String session);

    // 重置密码 邮箱验证
    String validateOnly(String email, String code, String sessionId); // 验证码验证

    // 重置密码
    boolean resetPassword(String password, String email);
}
