package com.learningdog.content;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: getjiajia
 * @description: content-service测试启动类
 * @version: 1.0
 */
@SpringBootApplication
@MapperScan("com.learningdog.content.mapper")
public class ContentTestStarter {
    public static void main(String[] args) {
        SpringApplication.run(ContentTestStarter.class,args);
    }
}
