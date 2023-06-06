package com.learningdog.messagesdk.service;

import com.learningdog.messagesdk.po.MqMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: getjiajia
 * @description: 消息处理抽象类
 * @version: 1.0
 */
@Slf4j
@Data
public abstract class MessageProcessAbstract {
    @Resource
    MqMessageService mqMessageService;

    /**
     * @param mqMessage:  执行的任务内容
     * @return boolean  true:处理成功，false处理失败
     * @author getjiajia
     * @description 任务处理的具体实现
     */
    public abstract boolean execute(MqMessage mqMessage);


    /**
     * @param shardIndex: 分片序号
     * @param shardTotal: 分片总数
     * @param messageType: 消息类型
     * @param count: 一次执行能够处理消息的数量
     * @param timeout: 预估任务执行时间,到此时间如果任务还没有结束则强制结束 单位秒
     * @return void
     * @author getjiajia
     * @description 扫描消息表多线程执行任务
     */
    public void process(int shardIndex,int shardTotal,String messageType,int count,long timeout){
        try{
            List<MqMessage> messageList = mqMessageService.getMessageList(shardIndex, shardTotal, messageType, count);
            int size=messageList.size();
            log.debug("取出待处理消息，共{}条",size);
            if (messageList==null||size==0){
                return;
            }
            //创建线程池
            Executor threadPool= Executors.newFixedThreadPool(size);
            //计数器
            CountDownLatch countDownLatch=new CountDownLatch(size);
            //将任务加入到线程池
            messageList.forEach(mqMessage -> {
                threadPool.execute(()->{
                    log.debug("开始执行任务：{}",mqMessage);
                    try{
                        boolean result = execute(mqMessage);
                        if (result){
                            log.debug("任务执行成功：{}",mqMessage);
                            //删除消息记录表，并添加到历史记录表中
                            int completed=mqMessageService.completed(mqMessage.getId());
                            if (completed<=0){
                                log.debug("更新消息记录失败：{}",mqMessage);
                            }else {
                                log.debug("更新消息记录成功：{}",mqMessage);
                            }
                        }else {
                            log.debug("任务执行失败：{}",mqMessage);
                        }
                        log.debug("任务完成：{}",mqMessage);
                    }catch (Exception e){
                        log.debug("任务出现异常：{}，任务：{}",e.getMessage(),mqMessage);
                    }finally {
                        countDownLatch.countDown();
                    }
                });
            });
            countDownLatch.await(timeout, TimeUnit.SECONDS);
            log.debug("一次消息完成，处理消息总数：{}",size);
        }catch (InterruptedException e) {
            log.error("中断异常：{}",e.getMessage());
        }
    }


}

