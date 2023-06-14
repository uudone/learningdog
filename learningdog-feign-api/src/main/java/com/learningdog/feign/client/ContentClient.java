package com.learningdog.feign.client;

import com.learningdog.content.po.CoursePublish;
import com.learningdog.feign.fallback.ContentClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: getjiajia
 * @description: 内容管理服务feign
 * @version: 1.0
 */
@FeignClient(value = "content-api"
        ,path = "/content",
        fallbackFactory = ContentClientFallbackFactory.class)
public interface ContentClient {

    @GetMapping("/coursepublish/{courseId}")
    public CoursePublish getCoursePublish(@PathVariable("courseId") Long courseId);
}
