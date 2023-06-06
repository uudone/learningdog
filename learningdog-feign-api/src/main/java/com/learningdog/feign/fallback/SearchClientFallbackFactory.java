package com.learningdog.feign.fallback;

import com.learningdog.feign.client.SearchClient;
import com.learningdog.search.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: getjiajia
 * @description: TODO
 * @version: 1.0
 */
@Component
@Slf4j
public class SearchClientFallbackFactory implements FallbackFactory<SearchClient> {
    @Override
    public SearchClient create(Throwable throwable) {
        return new SearchClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                log.debug("调用搜索add方法时发生熔断走降级方法,熔断异常:", throwable.getMessage());
                return false;
            }

            @Override
            public Boolean delete(Long id) {
                log.debug("调用搜索delete方法时发生熔断走降级方法,熔断异常:", throwable.getMessage());
                return false;
            }
        };
    }
}
