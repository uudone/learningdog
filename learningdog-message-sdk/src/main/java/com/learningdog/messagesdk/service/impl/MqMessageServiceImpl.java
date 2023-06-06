package com.learningdog.messagesdk.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.messagesdk.mapper.MqMessageHistoryMapper;
import com.learningdog.messagesdk.mapper.MqMessageMapper;
import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.messagesdk.po.MqMessageHistory;
import com.learningdog.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

    @Resource
    MqMessageMapper mqMessageMapper;
    @Resource
    MqMessageHistoryMapper mqMessageHistoryMapper;

    @Override
    public List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count) {
        return mqMessageMapper.getByShardIndex(shardIndex,shardTotal,messageType,count);
    }

    @Override
    public MqMessage addMessage(String messageType, String businessKey1, String businessKey2, String businessKey3) {
        MqMessage mqMessage=new MqMessage();
        mqMessage.setMessageType(messageType);
        mqMessage.setBusinessKey1(businessKey1);
        mqMessage.setBusinessKey2(businessKey2);
        mqMessage.setBusinessKey3(businessKey3);
        int insert=mqMessageMapper.insert(mqMessage);
        if (insert<=0){
            return null;
        }
        return mqMessage;
    }

    @Override
    @Transactional
    public int completed(long id) {
        MqMessage mqMessage=mqMessageMapper.selectById(id);
        if (mqMessage==null){
            return 0;
        }
        mqMessage.setState("1");
        MqMessageHistory mqMessageHistory=new MqMessageHistory();
        BeanUtils.copyProperties(mqMessage,mqMessageHistory);
        mqMessageHistory.setExecuteDate(LocalDateTime.now());
        //添加到历史表中
        int insert = mqMessageHistoryMapper.insert(mqMessageHistory);
        if (insert<=0){
            return 0;
        }
        //删除消息表
        int delete=mqMessageMapper.deleteById(id);
        if (delete<=0){
            throw new RuntimeException("删除消息表异常，id="+id);
        }
        return 1;
    }

    @Override
    public int completeStageOne(long id) {
        return mqMessageMapper.update(null,new LambdaUpdateWrapper<MqMessage>()
                .eq(MqMessage::getId,id)
                .set(MqMessage::getStageState1,"1"));
    }

    @Override
    public int completeStageTwo(long id) {
        return mqMessageMapper.update(null,new LambdaUpdateWrapper<MqMessage>()
                .eq(MqMessage::getId,id)
                .set(MqMessage::getStageState2,"1"));
    }

    @Override
    public int completeStageThree(long id) {
        return mqMessageMapper.update(null,new LambdaUpdateWrapper<MqMessage>()
                .eq(MqMessage::getId,id)
                .set(MqMessage::getStageState3,"1"));
    }

    @Override
    public int completeStageFour(long id) {
        return mqMessageMapper.update(null,new LambdaUpdateWrapper<MqMessage>()
                .eq(MqMessage::getId,id)
                .set(MqMessage::getStageState4,"1"));
    }

    @Override
    public int getStageOne(long id) {
        return Integer.parseInt(mqMessageMapper.selectById(id).getStageState1());
    }

    @Override
    public int getStageTwo(long id) {
        return Integer.parseInt(mqMessageMapper.selectById(id).getStageState2());
    }

    @Override
    public int getStageThree(long id) {
        return Integer.parseInt(mqMessageMapper.selectById(id).getStageState3());
    }

    @Override
    public int getStageFour(long id) {
        return Integer.parseInt(mqMessageMapper.selectById(id).getStageState4());
    }
}
