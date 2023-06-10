package com.learningdog.util;

import com.alibaba.fastjson.JSON;
import com.learningdog.auth.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author: getjiajia
 * @description: 获取当前用户身份工具类
 * @version: 1.0
 */
@Slf4j
public class SecurityUtils {
    public static User getUser(){
        try{
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof String){
                String userString=principal.toString();
                return JSON.parseObject(userString,User.class);
            }
        }catch (Exception e){
            log.error("获取当前登录用户身份出错:{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


}
