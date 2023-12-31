package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.AuthorizeService;
import org.hibernate.validator.constraints.Length;
import org.omg.PortableInterceptor.USER_EXCEPTION;
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
    private final String USERNAME_REGEX = "^[\u4e00-\u9fa5a-zA-Z0-9]+$";

    // 注册
    @PostMapping("/valid-register-email") // 参数是需要验证的
    public RestBean<String> validateRegisterEmail(@Pattern (regexp = EMAIL_REGEX) @RequestParam("email") String email
                                            ,HttpSession session) {
        String s = service.sendValidateEmail(email, session.getId(), false);
        if (s == null) {
            return RestBean.success("邮件已发送, 请注意查收");
        } else {
            System.out.println(s);
            return RestBean.failure(400, "邮件发送失败, 请联系管理员");
        }
    }

    // 重置
    @PostMapping("/valid-reset-email") // 参数是需要验证的
    public RestBean<String> validateResetEmail(@Pattern (regexp = EMAIL_REGEX) @RequestParam("email") String email
            ,HttpSession session) {
        String s = service.sendValidateEmail(email, session.getId(), true);
        if (s == null) {
            return RestBean.success("邮件已发送, 请注意查收");
        } else {
            System.out.println(s);
            return RestBean.failure(400, "邮件发送失败, 请联系管理员");
        }
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(@Pattern(regexp = USERNAME_REGEX) @Length(min = 2, max = 8) @RequestParam("username") String username,
                                         @RequestParam("password") @Length(min = 6, max = 16) String password,
                                         @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                         @RequestParam("code") @Length(min = 6, max = 6) String code,
                                         HttpSession session){
        String s = service.validateAndRegister(username, password, email, code, session.getId());
        if (s == null)
            return RestBean.success("注册成功");
        else
            return RestBean.failure(400, s);

    }

    /*
    * 1. 发送验证邮件
    * 2. 验证码是否正确, 正确就在Session中存一个标志
    * 3. 用户发起重置密码请求, 如果存在标志, 就成功重置
    * */
    @PostMapping("/start-reset")
    public RestBean<String> startRest(@Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
                                      @RequestParam("code") @Length(min = 6, max = 6) String code,
                                      HttpSession session){
        String s = service.validateOnly(email, code, session.getId());
        if (s == null) {
            session.setAttribute("reset-password", email);
            return RestBean.success();
        } else {
            return RestBean.failure(400, s);
        }
    }

    @PostMapping("/do-reset")
    public RestBean<String> resetPassword( @RequestParam("password") @Length(min = 6, max = 16) String password,
                                           HttpSession session){
        String email = (String) session.getAttribute("reset-password");
        if (email == null) return RestBean.failure(401, "请先完成邮箱验证");
        else if(service.resetPassword(password, email)){
            session.removeAttribute("reset-password");
            return RestBean.success("密码重置成功");
        } else
            return RestBean.failure(500, "内部错误, 请联系管理员");
    }
}
