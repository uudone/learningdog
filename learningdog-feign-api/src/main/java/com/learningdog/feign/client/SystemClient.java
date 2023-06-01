package com.learningdog.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: getjiajia
 * @description: 系统服务feign
 * @version: 1.0
 */
@FeignClient(value = "system-api",path = "/system")
public interface SystemClient {
}
