package com.learningdog.content.service;

import com.learningdog.feign.client.MediaClient;
import com.learningdog.feign.conf.MultipartSupportConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author: getjiajia
 * @description: media-api远程调用测试
 * @version: 1.0
 */
@SpringBootTest
public class MediaFeignTest {
    @Resource
    MediaClient mediaClient;


    @Test
    public void testUploadFile() throws IOException {
        MultipartFile multipartFile= MultipartSupportConfig.getMultipartFile(new File("C:\\Users\\LIJIAHAO\\Desktop\\test.html"));
        String objectName="120.html";
        mediaClient.uploadFile(multipartFile,null,objectName);
    }
}
