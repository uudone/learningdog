package com.learningdog.auth.service;

import com.learningdog.auth.model.dto.AuthParamsDto;
import com.learningdog.auth.model.dto.UserExt;

/**
 * @author: getjiajia
 * @description: 认证service
 * @version: 1.0
 */
public interface AuthService {

    /**
     * @param authParamsDto:
     * @return UserExt
     * @author getjiajia
     * @description 认证方法
     */
    UserExt execute(AuthParamsDto authParamsDto);
}
