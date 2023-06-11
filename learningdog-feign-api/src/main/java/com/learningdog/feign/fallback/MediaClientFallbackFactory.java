package com.learningdog.feign.fallback;

import com.learningdog.feign.client.MediaClient;
import com.learningdog.media.po.MediaFiles;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author: getjiajia
 * @description: TODO
 * @version: 1.0
 */
@Slf4j
@Component
public class MediaClientFallbackFactory implements FallbackFactory<MediaClient> {
    @Override
    public MediaClient create(Throwable throwable) {
        return new MediaClient() {
            @Override
            public MediaFiles getMediaFiles(String mediaFilesId) {
                log.debug("MediaFeign发生熔断走降级方法，方法：getMediaFiles，参数：mediaFilesId={}",mediaFilesId);
                return null;
            }

            @Override
            public String uploadFile(MultipartFile multipartFile, String folder, String objectName,String token){
                log.debug("MediaFeign发生熔断走降级方法，方法：uploadFile，参数：multipartFile={},folder={},objectName={},token={}",multipartFile,folder,objectName,token);
                return null;
            }

            @Override
            public void deleteMediaFile(String fileMd5) {
                log.debug("MediaFeign发生熔断走降级方法，方法：deleteMediaFile，参数：fileMd5={}",fileMd5);
            }

            @Override
            public void deleteCourseHtml(String filePath) {
                log.debug("MediaFeign发生熔断走降级方法，方法：deleteCourseHtml，参数：filePath={}",filePath);
            }
        };
    }
}
