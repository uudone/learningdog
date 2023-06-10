package com.learningdog.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learningdog.auth.mapper.UserMapper;
import com.learningdog.auth.mapper.UserRoleMapper;
import com.learningdog.auth.model.dto.AuthParamsDto;
import com.learningdog.auth.model.dto.UserExt;
import com.learningdog.auth.po.User;
import com.learningdog.auth.po.UserRole;
import com.learningdog.auth.service.AuthService;
import com.learningdog.auth.service.WxAuthService;
import com.learningdog.feign.client.CheckCodeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author: getjiajia
 * @description: 微信扫码认证服务
 * @version: 1.0
 */
@Service("wx_authservice")
@Slf4j
public class WxAuthServiceImpl implements AuthService, WxAuthService {
    @Value("${weixin.appid}")
    String appid;
    @Value("${weixin.secret}")
    String secret;
    @Resource
    UserMapper userMapper;
    @Resource
    RestTemplate restTemplate;
    @Resource
    UserRoleMapper userRoleMapper;
    @Resource(name = "wx_authservice")
    WxAuthService wxAuthService;
    @Resource
    CheckCodeClient checkCodeClient;

    @Override
    public UserExt execute(AuthParamsDto authParamsDto) {
        String checkcodekey = authParamsDto.getCheckcodekey();
        Boolean flag = checkCodeClient.verifyKey(checkcodekey);
        if (!flag){
            throw new RuntimeException("校验码不正确");
        }
        String username = authParamsDto.getUsername();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user==null){
            throw new RuntimeException("账号不存在");
        }
        UserExt userExt=new UserExt();
        BeanUtils.copyProperties(user,userExt);
        return userExt;
    }

    @Override
    public User wxAuth(String code,String state) {
        //校验state
        Boolean flag = checkCodeClient.verifyKey(state);
        if (!flag){
            return null;
        }
        Map<String,String> token_map= getAccessToken(code);
        if (token_map==null){
            return null;
        }
        String token=token_map.get("access_token");
        String openid=token_map.get("openid");
        Map<String,String> user_map=getUserInfo(token,openid);
        if (user_map==null){
            return null;
        }
        //添加用户到数据库
        User user=wxAuthService.addWxUserToDB(user_map);
        return user;
    }




    /**
     * @param code:
     * @return Map<String,String>: {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     * @author getjiajia
     * @description 根据授权码code获取令牌，并封装令牌中的数据
     */
    private Map<String, String> getAccessToken(String code) {
        String wxUrl_template="https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String wxUrl = String.format(wxUrl_template, appid, secret, code);
        log.info("调用微信接口申请access_token, url:{}", wxUrl);
        ResponseEntity<String> response = restTemplate.exchange(wxUrl, HttpMethod.GET, null, String.class);
        String result=response.getBody();
        log.info("调用微信接口申请access_token: 返回值:{}", result);
        Map<String,String> map= JSON.parseObject(result,Map.class);
        return map;
    }

    /**
     * @param access_token:
     * @param openid:
     * @return Map<String,String>:{
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "https://thirdwx.qlogo.cn/mmopen/sveqxibJxCfHe/0",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     * @author getjiajia
     * @description 根据令牌获取用户信息
     */
    private Map<String, String> getUserInfo(String access_token, String openid) {
        String wxUrl_template="https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String wxUrl=String.format(wxUrl_template,access_token,openid);
        log.info("调用微信接口申请用户信息, url:{}", wxUrl);
        ResponseEntity<String> response = restTemplate.exchange(wxUrl, HttpMethod.GET, null, String.class);
        //防止乱码问题
        String result=new String(response.getBody().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        log.info("调用微信接口申请用户信息: 返回值:{}", result);
        Map<String,String> map= JSON.parseObject(result,Map.class);
        return map;
    }

    @Override
    @Transactional
    public User addWxUserToDB(Map<String, String> user_map) {
        String unionid = user_map.get("unionid");
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getWxUnionid, unionid));
        //如果不是新用户
        if (user!=null){
            return user;
        }
        //如果是新用户
        String userId= UUID.randomUUID().toString();
        user=new User();
        user.setId(userId);
        user.setWxUnionid(unionid);
        user.setNickname(user_map.get("nickname"));
        user.setUserpic(user_map.get("headimgurl"));
        user.setName(user_map.get("nickname"));
        user.setUsername(user_map.get("nickname"));
        user.setPassword(unionid);
        user.setUtype("101001");//学生类型
        user.setStatus("1");
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
        UserRole userRole=new UserRole();
        userRole.setId(UUID.randomUUID().toString());
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setUserId(userId);
        userRole.setRoleId("17");
        userRoleMapper.insert(userRole);
        return user;
    }
}
