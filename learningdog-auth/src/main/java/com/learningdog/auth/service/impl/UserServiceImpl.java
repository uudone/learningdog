package com.learningdog.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.auth.mapper.UserMapper;
import com.learningdog.auth.model.dto.AuthParamsDto;
import com.learningdog.auth.model.dto.UserExt;
import com.learningdog.auth.po.User;
import com.learningdog.auth.service.AuthService;
import com.learningdog.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  自定义UserDetailsService用来对接Spring Security
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    @Resource
    UserMapper userMapper;
    @Resource
    ApplicationContext applicationContext;

    /**
     * @param s: AuthParamsDto类型的json数据
     * @return UserDetails
     * @author getjiajia
     * @description 根据用户名查询用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try {
            //将认证参数转为AuthParamsDto类型
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}",s);
            throw new RuntimeException("认证请求数据格式不对");
        }
        //开始认证
        String authType=authParamsDto.getAuthType();
        AuthService authService=applicationContext.getBean(authType+"_authservice",AuthService.class);
        UserExt userExt=authService.execute(authParamsDto);
        return getUserPrincipal(userExt);
    }

    /**
     * @param userExt:
     * @return UserDetails
     * @author getjiajia
     * @description 封装用户信息
     */
    public UserDetails getUserPrincipal(UserExt userExt){
        //用户权限
        String[] authorities={"p1"};
        String password=userExt.getPassword();
        //排除用户敏感信息
        userExt.setPassword(null);
        String jsonString=JSON.toJSONString(userExt);
        //创建UserDetails对象
        UserDetails userDetails= org.springframework.security.core.userdetails.User
                .withUsername(jsonString)
                .password(password)
                .authorities(authorities)
                .build();
        return userDetails;
    }
}
