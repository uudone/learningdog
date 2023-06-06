package com.learningdog.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: getjiajia
 * @description: content-service测试启动类
 * @version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.learningdog.content"
        ,"com.learningdog.messagesdk",
        "com.learningdog.feign"})
public class ContentTestStarter {
    public static void main(String[] args) {
        SpringApplication.run(ContentTestStarter.class,args);
    }
}
