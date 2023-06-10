package com.learningdog.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: getjiajia
 * @description: 认证服务feign
 * @version: 1.0
 */
@FeignClient(value = "auth-service",
        path = "/auth"
)
public interface AuthClient {
}
