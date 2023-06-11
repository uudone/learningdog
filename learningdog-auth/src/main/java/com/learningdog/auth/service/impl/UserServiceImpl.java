package com.learningdog.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.auth.mapper.MenuMapper;
import com.learningdog.auth.mapper.UserMapper;
import com.learningdog.auth.mapper.UserRoleMapper;
import com.learningdog.auth.model.dto.AuthParamsDto;
import com.learningdog.auth.model.dto.ResetPasswordDto;
import com.learningdog.auth.model.dto.UserExt;
import com.learningdog.auth.model.dto.UserRegisterDto;
import com.learningdog.auth.po.Menu;
import com.learningdog.auth.po.User;
import com.learningdog.auth.po.UserRole;
import com.learningdog.auth.service.AuthService;
import com.learningdog.auth.service.UserService;
import com.learningdog.base.code.RoleTypeID;
import com.learningdog.base.code.UserType;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.feign.client.CheckCodeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    MenuMapper menuMapper;
    @Resource
    ApplicationContext applicationContext;
    @Resource
    CheckCodeClient checkCodeClient;
    @Resource
    UserMapper userMapper;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    UserRoleMapper userRoleMapper;
    @Resource
    UserService userService;

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
        //查询用户权限
        List<Menu> menus = menuMapper.selectPermissionByUserId(userExt.getId());
        ArrayList<String> permissions=new ArrayList<>();
        if (menus==null||menus.size()==0){
            //用户权限,如果不加则报Cannot pass a null GrantedAuthority collection
            permissions.add("p1");
        }else{
            menus.forEach(menu -> {
                permissions.add(menu.getCode());
                    }
            );
        }
        String password=userExt.getPassword();
        //排除用户敏感信息
        userExt.setPassword(null);
        //设置用户权限
        userExt.setPermissions(permissions);
        String[] authorities=permissions.toArray(new String[0]);
        String jsonString=JSON.toJSONString(userExt);
        //创建UserDetails对象
        UserDetails userDetails= org.springframework.security.core.userdetails.User
                .withUsername(jsonString)
                .password(password)
                .authorities(authorities)
                .build();
        return userDetails;
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        String password=resetPasswordDto.getPassword();
        String confirmPwd=resetPasswordDto.getConfirmpwd();
        if (!password.equals(confirmPwd)){
            LearningdogException.cast("确认密码不一致");
        }
        String checkcode=resetPasswordDto.getCheckcode();
        String checkcodeKey=resetPasswordDto.getCheckcodekey();
        Boolean verify = checkCodeClient.verify(checkcodeKey, checkcode);
        if (!verify){
            LearningdogException.cast("验证码不正确");
        }
        String email=resetPasswordDto.getEmail();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (user==null){
            LearningdogException.cast("用户不存在");
        }
        //更新用户密码
        String pwEncode=passwordEncoder.encode(password);
        int update= userMapper.update(null,new LambdaUpdateWrapper<User>()
                .eq(User::getId,user.getId())
                .set(User::getPassword,pwEncode)
                .set(User::getUpdateTime,LocalDateTime.now()));
        if (update<=0){
            LearningdogException.cast("密码重置失败，请重试");
        }
    }

    @Override
    public void register(UserRegisterDto userRegisterDto) {
        String password=userRegisterDto.getPassword();
        String confirmPwd=userRegisterDto.getConfirmpwd();
        if (!password.equals(confirmPwd)){
            LearningdogException.cast("确认密码不一致");
        }
        String checkcode=userRegisterDto.getCheckcode();
        String checkcodekey=userRegisterDto.getCheckcodekey();
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);
        if (!verify){
            LearningdogException.cast("验证码不正确");
        }
        //将用户插入到数据库中
        userService.addUserToDB(userRegisterDto);
    }

    @Override
    @Transactional
    public void addUserToDB(UserRegisterDto userRegisterDto){
        String email=userRegisterDto.getEmail();
        String password=userRegisterDto.getPassword();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (user!=null){
            LearningdogException.cast("注册邮箱已存在，请换另一个邮箱使用");
        }
        user=new User();
        BeanUtils.copyProperties(userRegisterDto,user);
        //如果是新用户
        String userId=UUID.randomUUID().toString();
        user.setId(userId);
        user.setName(user.getNickname());
        user.setCreateTime(LocalDateTime.now());
        user.setUtype(UserType.STUDENT);//学生类型
        user.setStatus("1");
        user.setPassword(passwordEncoder.encode(password));
        userMapper.insert(user);
        UserRole userRole=new UserRole();
        userRole.setId(UUID.randomUUID().toString());
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setUserId(userId);
        userRole.setRoleId(RoleTypeID.STUDENT);//角色为学生
        userRoleMapper.insert(userRole);
    }

}
