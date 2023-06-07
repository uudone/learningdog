package com.learningdog.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 令牌配置
 * @version: 1.0
 */
@Configuration
public class TokenConfig {
    @Resource
    TokenStore tokenStore;

    @Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();
    }

    @Bean(name = "authorizationServerTokenServicesCustom")
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services=new DefaultTokenServices();
        services.setSupportRefreshToken(true);//支持刷新临牌
        services.setTokenStore(tokenStore);//令牌存储策略
        services.setAccessTokenValiditySeconds(60*60*2);//令牌默认有效期2小时
        services.setRefreshTokenValiditySeconds(60*60*24*3);//刷新令牌默认有效期3天
        return services;
    }
}
