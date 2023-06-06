package com.learningdog.messagesdk.mqTest;

import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.messagesdk.service.MessageProcessAbstract;
import com.learningdog.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: getjiajia
 * @description: 消息处理测试类，继承MessageProcessAbstract
 * @version: 1.0
 */
@Component
@Slf4j
public class MessageProcess extends MessageProcessAbstract {
    @Autowired
    MqMessageService mqMessageService;
    @Override
    public boolean execute(MqMessage mqMessage) {
        Long id=mqMessage.getId();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //取出阶段状态
        int stageOne = mqMessageService.getStageOne(id);
        if(stageOne<1){
            log.debug("开始执行第一阶段任务");
            int i = mqMessageService.completeStageOne(id);
            if(i>0){
                log.debug("完成第一阶段任务");
            }

        }else{
            log.debug("无需执行第一阶段任务");
        }

        //执行第二阶段任务....

        return true;
    }
}
