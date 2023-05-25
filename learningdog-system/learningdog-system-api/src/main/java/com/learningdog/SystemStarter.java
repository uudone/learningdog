package com.learningdog;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author: getjiajia
 * @description: System服务启动类
 * @version: 1.0
 */
@SpringBootApplication
@EnableSwagger2Doc
@EnableScheduling
public class SystemStarter {
    public static void main(String[] args) {
        SpringApplication.run(SystemStarter.class,args);
    }
}
