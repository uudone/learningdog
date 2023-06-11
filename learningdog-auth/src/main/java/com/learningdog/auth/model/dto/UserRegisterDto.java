package com.learningdog.auth.model.dto;

import io.swagger.annotations.ApiModel;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: getjiajia
 * @description: 用户注册dto
 * @version: 1.0
 */
@Data
@ApiModel(description = "用户注册dto")
public class UserRegisterDto {

    @ApiModelProperty(name = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String cellphone;
    @ApiModelProperty(name = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;
    @ApiModelProperty(name = "邮箱")
    @NotBlank(message = "邮箱不能为空")
    private String email;
    @ApiModelProperty(name = "昵称")
    @NotBlank(message = "昵称不能为空")
    private String nickname;
    @ApiModelProperty(name = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;
    @ApiModelProperty(name = "确认密码")
    @NotBlank(message = "确认密码不能为空")
    private String confirmpwd;
    @ApiModelProperty(name = "验证码key")
    @NotBlank(message = "验证码key不能为空")
    private String checkcodekey;
    @ApiModelProperty(name = "验证码")
    @NotBlank(message = "验证码不能为空")
    private String checkcode;
}
