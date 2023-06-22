package com.learningdog.content.jobhandler;

import com.alibaba.fastjson.JSON;
import com.learningdog.content.po.CoursePublish;
import com.learningdog.content.service.CoursePublishService;
import com.learningdog.feign.client.SearchClient;
import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.messagesdk.service.MessageProcessAbstract;
import com.learningdog.messagesdk.service.MqMessageService;
import com.learningdog.search.po.CourseIndex;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author: getjiajia
 * @description: 课程发布任务
 * @version: 1.0
 */
@Component
@Slf4j
public class CoursePublishTask extends MessageProcessAbstract {
    @Resource
    MqMessageService mqMessageService;
    @Resource
    CoursePublishService coursePublishService;
    @Resource
    SearchClient searchClient;
    @Resource
    RedisTemplate redisTemplate;


    @XxlJob("coursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception{
        int shardIndex= XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex="+shardIndex+",shardTotal="+shardTotal);
        process(shardIndex,shardTotal,"course_publish",10,60*5);
    }



    @Override
    public boolean execute(MqMessage mqMessage) {
        //获取消息中业务相关信息
        String businessKey1=mqMessage.getBusinessKey1();
        String businessKey2 = mqMessage.getBusinessKey2();
        String token=mqMessage.getBusinessKey3();
        long courseId=Long.parseLong(businessKey1);
        long companyId=Long.parseLong(businessKey2);
        //课程静态化
        generateCourseHtml(mqMessage,courseId,companyId,token);
        //课程索引
        saveCourseIndex(mqMessage,courseId,token);
        //课程缓存
        saveCourseCache(mqMessage,courseId);
        return true;
    }

    /**
     * @param mqMessage:
     * @param courseId:
     * @param companyId:
     * @param token:
     * @return void
     * @author getjiajia
     * @description 生成课程静态化页面并上传到文件系统
     */
    public void generateCourseHtml(MqMessage mqMessage,long courseId,long companyId,String token){
        log.debug("开始课程页面静态化，课程id:{}",courseId);
        long messageId=mqMessage.getId();
        int stageOne= mqMessageService.getStageOne(messageId);
        if (stageOne>0){
            log.debug("课程页面静态化已被处理，课程id:{}",courseId);
            return;
        }
        //执行页面静态化任务
        File file=coursePublishService.generateCourseHtml(courseId);
        if (file==null){
            throw new RuntimeException("课程页面静态化失败，courseId："+courseId);
        }
        //上传静态页面到minio中
        coursePublishService.uploadCourseHtml(companyId,courseId,file,token);
        //保存第一阶段状态
        mqMessageService.completeStageOne(messageId);
        log.debug("课程页面静态化成功，课程id:{}",courseId);
    }

    /**
     * @param mqMessage:
     * @param courseId:
     * @param token:
     * @return void
     * @author getjiajia
     * @description 保存课程索引信息
     */
    public void saveCourseIndex(MqMessage mqMessage,long courseId,String token){
        log.debug("开始保存课程索引信息,课程id:{}",courseId);
        long messageId=mqMessage.getId();
        int stageTwo=mqMessageService.getStageTwo(messageId);
        if (stageTwo>0){
            log.debug("课程课程索引信息已保存，课程id:{}",courseId);
            return;
        }
        //执行课程信息保存到es任务
        CoursePublish coursePublish=coursePublishService.getById(courseId);
        if (coursePublish==null){
            log.info("课程索引信息丢失，课程id:{}",courseId);
            return;
        }
        CourseIndex courseIndex=new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        boolean result= searchClient.add(courseIndex,token);
        if (!result){
            log.debug("课程索引信息保存失败课程id:{}",courseId);
            return;
        }
        mqMessageService.completeStageTwo(messageId);
        log.debug("课程索引信息保存成功，课程id:{}",courseId);
    }

    /**
     * @param mqMessage:
     * @param courseId:
     * @return void
     * @author getjiajia
     * @description 将课程信息缓存至redis
     */
    public void saveCourseCache(MqMessage mqMessage,long courseId){
        //执行课程缓存任务
        log.debug("开始将课程信息缓存至redis，课程id：{}",courseId);
        //执行缓存到redis任务
        Long messageId = mqMessage.getId();
        int stageThree = mqMessageService.getStageThree(messageId);
        if (stageThree>0){
            log.debug("课程课程信息已缓存到redis，课程id:{}",courseId);
            return;
        }
        //从数据库中查询数据
        CoursePublish coursePublish = coursePublishService.getById(courseId);
        if (coursePublish==null){
            log.info("课程缓存信息丢失，课程id:{}",courseId);
            return;
        }
        //将数据存入redis
        redisTemplate.opsForValue().set("course:publish:"+courseId, JSON.toJSONString(coursePublish),30, TimeUnit.MINUTES);
        //向布隆过滤器添加数据
        coursePublishService.addCourseIdToBloomFilter(courseId);
        mqMessageService.completeStageThree(messageId);
        log.debug("课程信息成功缓存至redis，课程id：{}",courseId);
    }
}
