package com.learningdog.content.util;

import com.alibaba.fastjson.JSON;
import com.learningdog.auth.po.User;
import com.learningdog.base.exception.LearningdogException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    /**
     * @param :
     * @return Long
     * @author getjiajia
     * @description 获取user下的companyId
     */
    public static Long getCompanyId(){
        User user=SecurityUtils.getUser();
        if (user==null){
            LearningdogException.cast("请登录后再操作");
        }
        String companyIdStr = user.getCompanyId();
        if (StringUtils.isBlank(companyIdStr)){
            LearningdogException.cast("没有任何机构权限，无法操作");
        }
        return Long.parseLong(companyIdStr);
    }



}
