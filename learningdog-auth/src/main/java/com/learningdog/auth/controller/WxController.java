package com.learningdog.auth.controller;

import com.learningdog.auth.po.User;
import com.learningdog.auth.service.WxAuthService;
import com.learningdog.feign.client.CheckCodeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: TODO
 * @version: 1.0
 */
@Controller
@Slf4j
public class WxController {
    @Resource(name="wx_authservice")
    WxAuthService wxAuthService;
    @Resource
    CheckCodeClient checkCodeClient;


    @RequestMapping("/wxLogin")
    public String wxLogin(String code,String state){
        log.debug("微信扫码回调,code:{},state:{}",code,state);
        //携带授权码code向微信申请令牌，拿到令牌查询用户信息，将用户信息写入本项目数据库
        User user=wxAuthService.wxAuth(code,state);
        if(user==null){
            return "redirect:http://www.51xuecheng.cn/error.html";
        }
        String wxcode = checkCodeClient.generateKey("wxcode");
        return "redirect:http://www.51xuecheng.cn/sign.html?username="+user.getUsername()+"&authType=wx"+"&checkcodekey="+wxcode;

    }
}
