package com.learningdog.auth.service;

import com.learningdog.auth.po.User;

import java.util.Map;

/**
 * @author: getjiajia
 * @description: 微信认证服务类
 * @version: 1.0
 */
public interface WxAuthService {

    /**
     * @param code:
     * @return User
     * @author getjiajia
     * @description 微信认证接口，携带授权码到微信获取用户信息，并封装成User对象
     */
    User wxAuth(String code,String state);

    /**
     * @param user_map:
     * @return User
     * @author getjiajia
     * @description 添加wx用户信息到数据库中
     */
    User addWxUserToDB(Map<String, String> user_map);
}
