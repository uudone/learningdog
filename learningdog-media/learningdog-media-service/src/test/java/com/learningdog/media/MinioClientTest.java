package com.learningdog.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author: getjiajia
 * @description: 测试使用minioSDK上传、删除、下载文件
 * @version: 1.0
 */
public class MinioClientTest {

    private MinioClient minioClient;

    @BeforeEach
    public void createMinioClient(){
        minioClient=MinioClient.builder()
                .endpoint("http://47.113.228.251:9000")
                .credentials("access123456","access123456")
                .build();
    }
    @Test
    public void testUploadFile(){
        ContentInfo contentInfo= ContentInfoUtil.findExtensionMatch(".txt");
        String mimeType= MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用字节流
        if (contentInfo!=null){
            mimeType=contentInfo.getMimeType();
        }
        try {
            UploadObjectArgs args=UploadObjectArgs.builder()
                    .bucket("test")
                    .object("/0001/log")//bucket下的子目录
                    .filename("C:\\file\\logback\\1.txt")//本地文件目录
                    .contentType(mimeType)//默认根据文件扩展名称推断，也可以自己指定
                    .build();
            minioClient.uploadObject(args);
        } catch (IOException e) {
            System.out.println("文件不存在");
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("上传失败");
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDeleteFile(){
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("test")
                            .object("/0001/log")
                            .build()
            );
        } catch (Exception e) {
            System.out.println("删除失败");
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDownloadFile(){
        try{
            GetObjectArgs args=GetObjectArgs.builder()
                    .bucket("test")
                    .object("/0001/wow.png")
                    .build();
            FilterInputStream inputStream=minioClient.getObject(args);
            FileOutputStream outputStream=new FileOutputStream("C:\\Users\\LIJIAHAO\\Desktop\\2.png");
            IOUtils.copy(inputStream,outputStream);
            //DigestUtils.md5Hex()
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
