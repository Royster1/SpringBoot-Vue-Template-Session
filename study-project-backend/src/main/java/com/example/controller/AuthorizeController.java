package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Pattern;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthorizeController {

    // 验证码验证
    @Resource
    AuthorizeService service;

    private final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,}$";

    @PostMapping("/valid-email") // 参数是需要验证的
    public RestBean<String> validateEmail(@Pattern (regexp = EMAIL_REGEX) @RequestParam("email") String email
                                            ,HttpSession session) {
        if (service.sendValidateEmail(email, session.getId())) {
            return RestBean.success("邮件已发送, 请注意查收");
        } else {
            return RestBean.failure(400, "邮件发送失败, 请联系管理员");
        }
    }
}
