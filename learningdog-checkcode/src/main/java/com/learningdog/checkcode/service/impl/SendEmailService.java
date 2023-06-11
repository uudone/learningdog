package com.learningdog.checkcode.service.impl;

import com.learningdog.checkcode.model.dto.CheckCodeParamsDto;
import com.learningdog.checkcode.model.dto.CheckCodeResultDto;
import com.learningdog.checkcode.service.AbstractCheckCodeService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: getjiajia
 * @description: 邮件发送服务
 * @version: 1.0
 */
@Service("sendEmailService")
public class SendEmailService extends AbstractCheckCodeService {
    @Value("${email.register.key}")
    String registerKey;
    @Value("${email.resetpw.key}")
    String resetpwKey;
    @Value("${email.exchange}")
    String emailExchange;
    @Resource
    RabbitTemplate rabbitTemplate;

    @Override
    @Resource(name = "numberLetterCheckCodeGenerator")
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator=checkCodeGenerator;
    }

    @Override
    @Resource(name = "UUIDKeyGenerator")
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator=keyGenerator;
    }

    @Override
    @Resource(name = "redisCheckCodeStore")
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore=checkCodeStore;
    }

    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        //生成验证码并存储到redis中
        GenerateResult generate = generate(checkCodeParamsDto, 4, "email:", 60 * 5);
        //获取收件人
        String to=checkCodeParamsDto.getParam1();
        //获取生成的key和code
        String key=generate.getKey();
        String code=generate.getCode();
        //封装邮件内容
        Map<String,String> map=new HashMap<>();
        map.put("to",to);
        map.put("content",code);
        //将邮件信息发送到消息队列中
        if ("register".equals(checkCodeParamsDto.getCheckCodeType())){
            rabbitTemplate.convertAndSend(emailExchange,registerKey,map);
        }else if ("resetpassword".equals(checkCodeParamsDto.getCheckCodeType())){
            rabbitTemplate.convertAndSend(emailExchange,resetpwKey,map);
        }
        //封装返回对象
        CheckCodeResultDto resultDto=new CheckCodeResultDto();
        resultDto.setKey(key);
        return resultDto;
    }

    @Override
    public CheckCodeResultDto generateKey(String prefix) {
        return null;
    }
}
