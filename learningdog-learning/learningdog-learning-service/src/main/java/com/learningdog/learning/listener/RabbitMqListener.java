package com.learningdog.learning.listener;

import com.alibaba.fastjson.JSON;
import com.learningdog.base.code.OrderType;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.learning.service.ChooseCourseService;
import com.learningdog.messagesdk.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: rabbitmq监听器
 * @version: 1.0
 */
@Component
@Slf4j
public class RabbitMqListener {
    @Resource
    ChooseCourseService chooseCourseService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "learning.paynotify.queue"),
            exchange =@Exchange(name = "paynotify.fanout",type = ExchangeTypes.FANOUT)

    ))
    public void receivePayResult(Message message){
        MqMessage mqMessage= JSON.parseObject(message.getBody(),MqMessage.class);
        log.debug("学习中心服务接收支付结果:{}", mqMessage);
        String messageType = mqMessage.getMessageType();
        String chooseCourseId = mqMessage.getBusinessKey1();
        String orderType=mqMessage.getBusinessKey2();
        //只处理课程订单的支付结果
        if ("paynotify".equals(messageType)&& OrderType.BUY_COURSE.equals(orderType)){
            boolean b = chooseCourseService.finishPayChargeCourse(chooseCourseId);
            if (!b){
                //添加选课失败，抛出异常，本地重试，超过次数后将失败消息投递到指定的交换机
                LearningdogException.cast("收到支付结果，添加选课失败");
            }
        }
    }

}
