package com.learningdog.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;

/**
 * @author: getjiajia
 * @description: 安全配置类
 * @version: 1.0
 */
@EnableWebFluxSecurity
@Component
public class SecurityConfig {

    //安全拦截配置
    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http){
        return http.authorizeExchange()
                .pathMatchers("/**").permitAll()
                .anyExchange().authenticated()
                .and().csrf().disable().build();
    }
}
