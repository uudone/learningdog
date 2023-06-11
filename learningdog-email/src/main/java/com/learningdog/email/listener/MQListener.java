package com.learningdog.email.listener;

import com.learningdog.email.util.EmailUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: getjiajia
 * @description: 消息队列监听器
 * @version: 1.0
 */
@Component
public class MQListener {

    @Value("${email.from}")
    String from;
    @Value("${email.secret}")
    String secret;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "register.queue"),
            exchange = @Exchange(name = "email.direct",type = "direct"),
            key = "register"
    ))
    public void sendRegisterEmail(Map<String,String> map){
        String to=map.get("to");
        String content=map.get("content");
        EmailUtils.setEmail(from,to,"学习汪注册邮箱验证",content,secret);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "resetpw.queue"),
            exchange = @Exchange(name = "email.direct",type = "direct"),
            key = "resetpw"
    ))
    public void sendResetPasswordEmail(Map<String,String> map){
        String to=map.get("to");
        String content=map.get("content");
        EmailUtils.setEmail(from,to,"学习汪重置密码邮箱验证",content,secret);
    }
}
