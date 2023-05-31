package com.learningdog.media.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author: getjiajia
 * @description: 任务信息表测试类
 * @version: 1.0
 */
@SpringBootTest
public class ProcessTest {
    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Test
    public void testSetProcessTimeout(){
        LocalDateTime localDateTime=LocalDateTime.now();
        int update= mediaProcessMapper.setProcessTimeout(60*49,localDateTime);
        System.out.println(update);
    }
}
