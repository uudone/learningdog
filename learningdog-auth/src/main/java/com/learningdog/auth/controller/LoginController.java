package com.learningdog.auth.controller;

import com.learningdog.auth.mapper.UserMapper;
import com.learningdog.auth.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 认证授权测试控制器
 * @version: 1.0
 */
@RestController
@Slf4j
public class LoginController {

    @Resource
    UserMapper userMapper;

    @RequestMapping("/login-success")
    public String loginSuccess() {

        return "登录成功";
    }


    @RequestMapping("/user/{id}")
    public User getuser(@PathVariable("id") String id) {
        User xcUser = userMapper.selectById(id);
        return xcUser;
    }

    @RequestMapping("/r/r1")
    @PreAuthorize("hasAnyAuthority('p1')")
    public String r1() {
        return "访问r1资源";
    }

    @RequestMapping("/r/r2")
    @PreAuthorize("hasAnyAuthority('p2')")
    public String r2() {
        return "访问r2资源";
    }
}
