package com.learningdog.checkcode.controller;

import com.learningdog.checkcode.model.dto.CheckCodeParamsDto;
import com.learningdog.checkcode.model.dto.CheckCodeResultDto;
import com.learningdog.checkcode.service.impl.SendEmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 邮箱验证接口
 * @version: 1.0
 */
@Api("邮箱验证接口")
@RestController
@RequestMapping("/email")
public class EmailController {
    @Resource
    SendEmailService sendEmailService;

    @ApiOperation("注册请求邮箱验证")
    @PostMapping("/register")
    public CheckCodeResultDto registerEmail(CheckCodeParamsDto paramsDto){
        paramsDto.setCheckCodeType("register");
        return sendEmailService.generate(paramsDto);
    }

    @ApiOperation("重置密码请求邮箱验证")
    @PostMapping("/resetpassword")
    public CheckCodeResultDto resetpasswordEmail(CheckCodeParamsDto paramsDto){
        paramsDto.setCheckCodeType("resetpassword");
        return sendEmailService.generate(paramsDto);
    }
}
