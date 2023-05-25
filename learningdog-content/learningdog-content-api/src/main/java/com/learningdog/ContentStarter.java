package com.learningdog;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: getjiajia
 * @description: 内容管理服务启动类
 * @version: 1.0
 */
@SpringBootApplication
@EnableSwagger2Doc
public class ContentStarter {
    public static void main(String[] args) {
        SpringApplication.run(ContentStarter.class,args);
    }
}
