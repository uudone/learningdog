package com.learningdog.feign.client;

import com.learningdog.feign.fallback.CheckCodeClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: getjiajia
 * @description: 验证码服务feign
 * @version: 1.0
 */
@FeignClient(value = "checkcode",
        path = "/checkcode",
        fallbackFactory = CheckCodeClientFallbackFactory.class
)
public interface CheckCodeClient {

    @PostMapping(value = "/verify")
    public Boolean verify(@RequestParam("key") String key,@RequestParam("code") String code);

    @PostMapping(value = "/verifyKey")
    public Boolean verifyKey(@RequestParam("key") String key);

    @PostMapping(value = "/genKey")
    public String generateKey(@RequestParam("prefix")String prefix);
}

