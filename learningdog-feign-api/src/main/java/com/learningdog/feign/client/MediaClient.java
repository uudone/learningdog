package com.learningdog.feign.client;

import com.learningdog.feign.fallback.MediaClientFallbackFactory;
import com.learningdog.media.po.MediaFiles;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: getjiajia
 * @description: 媒资服务feign
 * @version: 1.0
 */
@FeignClient(value = "media-api",
        path = "/media",
        fallbackFactory = MediaClientFallbackFactory.class)
public interface MediaClient {

    @GetMapping("/files/{mediaFilesId}")
    public MediaFiles getMediaFiles(@PathVariable("mediaFilesId")String mediaFilesId);

}
