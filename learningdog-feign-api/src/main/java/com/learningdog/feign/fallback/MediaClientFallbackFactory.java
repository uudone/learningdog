package com.learningdog.feign.fallback;

import com.learningdog.feign.client.MediaClient;
import com.learningdog.media.po.MediaFiles;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: getjiajia
 * @description: TODO
 * @version: 1.0
 */
@Slf4j
public class MediaClientFallbackFactory implements FallbackFactory<MediaClient> {
    @Override
    public MediaClient create(Throwable throwable) {
        return new MediaClient() {
            @Override
            public MediaFiles getMediaFiles(String mediaFilesId) {
                log.info("MediaFeign远程调用失败，方法：getMediaFiles，参数：mediaFilesId={}",mediaFilesId);
                return null;
            }

            @Override
            public String uploadFile(MultipartFile multipartFile, String folder, String objectName){
                log.info("MediaFeign远程调用失败，方法：uploadFile，参数：multipartFile={},folder={},objectName={}",multipartFile,folder,objectName);
                return null;
            }
        };
    }
}
