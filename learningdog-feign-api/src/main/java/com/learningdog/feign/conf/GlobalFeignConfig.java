package com.learningdog.feign.conf;

import com.learningdog.feign.fallback.MediaClientFallbackFactory;
import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: getjiajia
 * @description: 开启feign配置类
 * @version: 1.0
 */
@Configuration
@EnableFeignClients(basePackages = "com.learningdog.feign.client")
public class GlobalFeignConfig {


}
