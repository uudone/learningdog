package com.learningdog.system.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: getjiajia
 * @description: MybatisPlus分页配置
 * @version: 1.0
 */
@Configuration
@MapperScan("com.learningdog.system.mapper")
public class MybatisPlusConfig {

    /**
     * @param :
     * @return MybatisPlusInterceptor
     * @author getjiajia
     * @description MybatisPlus分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor=new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
