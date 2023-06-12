package com.learningdog.feign.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author: getjiajia
 * @description: 选课学习服务feign
 * @version: 1.0
 */
@FeignClient(value = "learning-api",path = "/learning")
public interface LearningClient {
}
