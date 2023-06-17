package com.learningdog.order.config;

import com.alibaba.fastjson.JSON;
import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * @author: getjiajia
 * @description: rabbitmq配置类
 * @version: 1.0
 */
@Slf4j
@Configuration
public class RabbitMqConfig implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate=applicationContext.getBean(RabbitTemplate.class);
        MqMessageService mqMessageService=applicationContext.getBean(MqMessageService.class);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            // 投递失败，记录日志
            log.info("消息发送失败，应答码{}，原因{}，交换机{}，路由键{},消息{}",
                    replyCode, replyText, exchange, routingKey, message.toString());
            MqMessage mqMessage= JSON.parseObject(message.getBody(),MqMessage.class);
            //保存投递失败的数据到数据库
            mqMessageService.addMessage(mqMessage.getMessageType(),
                    mqMessage.getBusinessKey1(),
                    mqMessage.getBusinessKey2(),
                    mqMessage.getBusinessKey3());

        });
    }
}
