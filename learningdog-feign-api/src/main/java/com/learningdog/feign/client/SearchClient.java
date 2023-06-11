package com.learningdog.feign.client;

import com.learningdog.feign.fallback.SearchClientFallbackFactory;
import com.learningdog.search.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: getjiajia
 * @description: 搜索功能fegin
 * @version: 1.0
 */
@FeignClient(value = "search",
        path = "/search",
        fallbackFactory = SearchClientFallbackFactory.class)
public interface SearchClient {

    @PostMapping("/index/course")
    Boolean add(@RequestBody CourseIndex courseIndex, @RequestHeader(name = "authorization",required = false)String authorization);

    @DeleteMapping("/index/course/delete/{id}")
    Boolean delete(@PathVariable("id")Long id);
}
