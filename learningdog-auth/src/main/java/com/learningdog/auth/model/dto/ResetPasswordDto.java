package com.learningdog.auth.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: getjiajia
 * @description: 重置密码dto
 * @version: 1.0
 */
@Data
@ApiModel(description = "重置密码dto")
public class ResetPasswordDto {
    @ApiModelProperty(name = "手机号码")
    private String cellphone;
    @NotBlank(message = "邮箱不能为空")
    @ApiModelProperty(name = "邮箱")
    private String email;
    @NotBlank(message = "验证码key不能为空")
    @ApiModelProperty(name = "验证码key")
    private String checkcodekey;
    @NotBlank(message = "验证码不能为空")
    @ApiModelProperty(name = "验证码")
    private String checkcode;
    @NotBlank(message = "确认密码不能为空")
    @ApiModelProperty(name = "确认密码")
    private String confirmpwd;
    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(name = "密码")
    private String password;

}
