package com.learningdog.feign.fallback;

import com.learningdog.content.po.CoursePublish;
import com.learningdog.feign.client.ContentClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: getjiajia
 * @description: ContentClientFallbackFactory
 * @version: 1.0
 */
@Component
@Slf4j
public class ContentClientFallbackFactory implements FallbackFactory<ContentClient> {
    @Override
    public ContentClient create(Throwable throwable) {
        return new ContentClient() {
            @Override
            public CoursePublish getCoursePublish(Long courseId) {
                log.debug("ContentFeign发生熔断走降级方法，方法：getCoursePublish，参数：courseId={}",courseId);
                return null;
            }
        };
    }
}
