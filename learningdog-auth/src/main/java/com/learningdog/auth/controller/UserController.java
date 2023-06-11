package com.learningdog.auth.controller;

import com.learningdog.auth.model.dto.ResetPasswordDto;
import com.learningdog.auth.model.dto.UserRegisterDto;
import com.learningdog.auth.service.UserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api("用户管理接口")
public class UserController {

    @Resource
    UserService  userService;

    @PostMapping("/resetpassword")
    public void resetPassword(@RequestBody @Validated ResetPasswordDto resetPasswordDto){
        userService.resetPassword(resetPasswordDto);
    }

    @PostMapping("/register")
    public void register(@RequestBody @Validated UserRegisterDto userRegisterDto){
        userService.register(userRegisterDto);
    }
}
