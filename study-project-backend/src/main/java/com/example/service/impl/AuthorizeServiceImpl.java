package com.example.service.impl;

import com.example.entity.Account;
import com.example.mapper.UserMapper;
import com.example.service.AuthorizeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException; //导入依赖的package包/类

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;


// 权限校验服务
@Service
public class AuthorizeServiceImpl implements AuthorizeService {

    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender;

    @Value("${spring.mail.username}")
    String from;

    @Resource
    StringRedisTemplate template;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null)
            throw new UsernameNotFoundException("用户名不能为空");
        Account account = mapper.findAccountByNameOrEmail(username);
        if (account == null) // 查不到
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    /*
     * 1. 先生成对应的验证码
     * 2. 把邮箱和对应的验证码直接放到Redis里面(过期时间3分钟, 如果此时重新要求发邮件
     * 那么只要剩余时间低于2分钟, 就可以重新发送一次, 重复此流程)
     * 3. 发送验证码到指定邮箱
     * 4. 如果发送失败, 把Redis里面的刚刚插入的删除
     * 5. 用户在注册时, 再从Redis里面取出对应键值对, 然后看验证码是否一致
     * */
    @Override
    public boolean sendValidateEmail(String email, String sessionId) {
        String key = "email:" + sessionId + ":" + email;
        if (Boolean.TRUE.equals(template.hasKey(key))) {
            Long expire = Optional.ofNullable(template.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120) return false;
        }
        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("您的验证邮件");
        message.setText("验证码是: " + code);
        try {
            mailSender.send(message);
            template.opsForValue().set(key, String.valueOf(code), 3, TimeUnit.MINUTES);
            return true;
        } catch (MailException e) {
            e.printStackTrace();
            return false;
        }
    }
}
