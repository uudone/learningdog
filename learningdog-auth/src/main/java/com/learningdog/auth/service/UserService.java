package com.learningdog.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.auth.model.dto.ResetPasswordDto;
import com.learningdog.auth.model.dto.UserRegisterDto;
import com.learningdog.auth.po.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-06-07
 */
public interface UserService extends IService<User> {

    /**
     * @param resetPasswordDto:
     * @return void
     * @author getjiajia
     * @description 重置密码
     */
    void resetPassword(ResetPasswordDto resetPasswordDto);

    /**
     * @param userRegisterDto:
     * @return void
     * @author getjiajia
     * @description 用户注册
     */
    void register(UserRegisterDto userRegisterDto);

    /**
     * @param userRegisterDto:
     * @return void
     * @author getjiajia
     * @description 添加新用户到数据库
     */
    void addUserToDB(UserRegisterDto userRegisterDto);
}
