package com.learningdog.learning.service;

import com.learningdog.content.po.CoursePublish;
import com.learningdog.feign.client.ContentClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: contentClient测试
 * @version: 1.0
 */
@SpringBootTest
public class ContentClientTest {

    @Resource
    ContentClient contentClient;
    @Test
    public void testGetCoursePublish(){
        CoursePublish coursePublish = contentClient.getCoursePublish(2L);
        System.out.println(coursePublish);
    }
}
