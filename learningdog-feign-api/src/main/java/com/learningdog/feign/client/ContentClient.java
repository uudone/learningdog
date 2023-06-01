package com.learningdog.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: getjiajia
 * @description: 内容管理服务feign
 * @version: 1.0
 */
@FeignClient(value = "content-api",path = "/content")
public interface ContentClient {
}
