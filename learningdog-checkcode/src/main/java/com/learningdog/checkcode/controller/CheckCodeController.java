package com.learningdog.checkcode.controller;

import com.learningdog.checkcode.model.dto.CheckCodeParamsDto;
import com.learningdog.checkcode.model.dto.CheckCodeResultDto;
import com.learningdog.checkcode.service.CheckCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 验证码服务接口
 * @version: 1.0
 */
@Api(value = "验证码服务接口")
@RestController
public class CheckCodeController {
    @Resource(name="picCheckCodeService")
    CheckCodeService picCheckCodeService;

    @ApiOperation(value="生成验证信息", notes="生成验证信息")
    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto){
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    @ApiOperation(value="生成验证key", notes="生成验证key")
    @PostMapping(value = "/genKey")
    public String generateKey(@RequestParam("prefix")String prefix){
        return picCheckCodeService.generateKey(prefix).getKey();
    }

    @ApiOperation(value="校验", notes="校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType="query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType="query")
    })
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code){
        return picCheckCodeService.verify(key,code);
    }

    @ApiOperation(value="校验key", notes="校验key")
    @PostMapping(value = "/verifyKey")
    public Boolean verifyKey(String key){
        return picCheckCodeService.verifyKey(key);
    }
}
