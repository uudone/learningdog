package com.learningdog.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learningdog.auth.mapper.UserMapper;
import com.learningdog.auth.model.dto.AuthParamsDto;
import com.learningdog.auth.model.dto.UserExt;
import com.learningdog.auth.po.User;
import com.learningdog.auth.service.AuthService;
import com.learningdog.base.utils.StringUtil;
import com.learningdog.feign.client.CheckCodeClient;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 密码认证service实现类
 * @version: 1.0
 */
@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {
    @Resource
    UserMapper userMapper;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    CheckCodeClient checkCodeClient;

    @Override
    public UserExt execute(AuthParamsDto authParamsDto) {
        //校验验证码
        String checkcodekey = authParamsDto.getCheckcodekey();
        String checkcode = authParamsDto.getCheckcode();
        if (StringUtil.isBlank(checkcodekey)||StringUtil.isBlank(checkcode)){
            throw new RuntimeException("验证码为空");
        }
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (!verify){
            throw new RuntimeException("验证码输入错误");
        }
        //校验账号密码
        String username=authParamsDto.getUsername();
        User user=userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername,username));
        if (user==null){
            throw new RuntimeException("账号不存在");
        }
        UserExt userExt=new UserExt();
        BeanUtils.copyProperties(user,userExt);
        //校验密码
        String passwordFromDB=user.getPassword();
        String passwordFromInput=authParamsDto.getPassword();
        boolean matches=passwordEncoder.matches(passwordFromInput,passwordFromDB);
        if (!matches){
            throw new RuntimeException("账号或密码错误");
        }
        return userExt;
    }
}
