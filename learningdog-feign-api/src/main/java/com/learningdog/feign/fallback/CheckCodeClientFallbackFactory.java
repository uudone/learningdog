package com.learningdog.feign.fallback;

import com.learningdog.feign.client.CheckCodeClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: getjiajia
 * @description: CheckCodeClientFallbackFactory
 * @version: 1.0
 */
@Slf4j
@Component
public class CheckCodeClientFallbackFactory implements FallbackFactory<CheckCodeClient> {
    @Override
    public CheckCodeClient create(Throwable throwable) {
        return new CheckCodeClient() {
            @Override
            public Boolean verify(String key, String code) {
                log.debug("CheckCodeFeign发生熔断走降级方法，方法：verify，参数：key={},code={}",key,code);
                return false;
            }

            @Override
            public Boolean verifyKey(String key) {
                log.debug("CheckCodeFeign发生熔断走降级方法，方法：verifyKey，参数：key={}",key);
                return false;
            }

            @Override
            public String generateKey(String prefix) {
                log.debug("CheckCodeFeign发生熔断走降级方法，方法：generateKey，参数：prefix={}",prefix);
                return null;
            }
        };
    }
}
