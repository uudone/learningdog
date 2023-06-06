package com.learningdog.messagesdk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.messagesdk.po.MqMessage;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-06-02
 */
public interface MqMessageService extends IService<MqMessage> {

    /**
     * @param shardIndex: 分片序号
     * @param shardTotal: 分片总数
     * @param messageType: 消息类型
     * @param count:  分页参数
     * @return List<MqMessage>
     * @author getjiajia
     * @description 扫描消息表记录
     */
    List<MqMessage> getMessageList(int shardIndex, int shardTotal,String messageType,int count);

    /**
     * @param messageType: 消息类型
     * @param businessKey1: 业务1
     * @param businessKey2: 业务2
     * @param businessKey3: 业务3
     * @return MqMessage
     * @author getjiajia
     * @description 添加消息
     */
    MqMessage addMessage(String messageType,String businessKey1,String businessKey2,String businessKey3);

    /**
     * @param id:  消息记录id
     * @return int
     * @author getjiajia
     * @description 完成任务
     */
    int completed(long id);

    /**
     * @param id:  任务id
     * @return int
     * @author getjiajia
     * @description 完成第i阶段任务
     */
    int completeStageOne(long id);
    int completeStageTwo(long id);
    int completeStageThree(long id);
    int completeStageFour(long id);

    /**
     * @param id: 任务id
     * @return int
     * @author getjiajia
     * @description 获取第i个阶段任务的状态
     */
    int getStageOne(long id);
    int getStageTwo(long id);
    int getStageThree(long id);
    int getStageFour(long id);



}
