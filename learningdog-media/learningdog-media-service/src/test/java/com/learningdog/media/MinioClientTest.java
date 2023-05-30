package com.learningdog.media;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test
    public void uploadChunkFile() throws Exception {
        String chunkFolderPath="C:\\Users\\LIJIAHAO\\Desktop\\file\\obs\\chunk\\";
        File chunkFolder=new File(chunkFolderPath);
        File[] files=chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            UploadObjectArgs args= UploadObjectArgs.builder()
                    .bucket("test")
                    .object("chunk/".concat(files[i].getName()))
                    .filename(files[i].getAbsolutePath())
                    .build();
            minioClient.uploadObject(args);
            System.out.println("上传文件分块"+i+"成功");
        }
    }

    @Test
    public void mergeMinioFile() throws Exception{
        List<ComposeSource> sources= Stream.iterate(0,i->++i)
                .limit(9)
                .map(i->ComposeSource.builder()
                        .bucket("test")
                        .object("chunk/".concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        ComposeObjectArgs composeObjectArgs=ComposeObjectArgs.builder()
                .bucket("test")
                .object("merge1.mp4")
                .sources(sources)
                .build();
        minioClient.composeObject(composeObjectArgs);
    }


    @Test
    public void removeChunk(){
        List<DeleteObject> deleteObjects=Stream.iterate(1,i->++i)
                .limit(8)
                .map(i->new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs=RemoveObjectsArgs.builder()
                .bucket("test")
                .objects(deleteObjects)
                .build();

        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError=null;
            try{
                deleteError=r.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
                });
    }
}
