package com.learningdog.checkcode.service.impl;

import com.learningdog.checkcode.service.CheckCodeService;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author: getjiajia
 * @description: uuid生成器
 * @version: 1.0
 */
@Component("UUIDKeyGenerator")
public class UUIDKeyGenerator implements CheckCodeService.KeyGenerator {
    @Override
    public String generate(String prefix) {
        String uuid= UUID.randomUUID().toString();
        return prefix+uuid.replaceAll("-","");
    }
}
