package com.learningdog.messagesdk.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: getjiajia
 * @description: Mybatis-Plus 配置
 * @version: 1.0
 */
@Configuration("message-mybatis-conf")
@MapperScan("com.learningdog.messagesdk.mapper")
public class MybatisPlusConfig {
}
