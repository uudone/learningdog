package com.learningdog.content.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: redisTemplate测试类
 * @version: 1.0
 */
@SpringBootTest
public class RedisTests {
    @Resource
    RedisTemplate redisTemplate;

    @Test
    public void delete(){
        redisTemplate.delete("course:publish:122");
    }
}
