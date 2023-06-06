package com.learningdog.feign.client;

import com.learningdog.feign.conf.MultipartSupportConfig;
import com.learningdog.feign.fallback.MediaClientFallbackFactory;
import com.learningdog.media.po.MediaFiles;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author: getjiajia
 * @description: 媒资服务feign
 * @version: 1.0
 */
@FeignClient(value = "media-api",
        path = "/media",
        fallbackFactory = MediaClientFallbackFactory.class,
        configuration = MultipartSupportConfig.class)
public interface MediaClient {

    @GetMapping("/files/{mediaFilesId}")
    public MediaFiles getMediaFiles(@PathVariable("mediaFilesId")String mediaFilesId);


    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFile(@RequestPart("filedata") MultipartFile multipartFile,
                                          @RequestParam(value = "folder",required = false)String folder,
                                          @RequestParam(value = "objectName",required = false)String objectName);
}