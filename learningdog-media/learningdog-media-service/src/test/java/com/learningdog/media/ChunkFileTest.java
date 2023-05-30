package com.learningdog.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author: getjiajia
 * @description: 文件分块合并测试
 * @version: 1.0
 */
public class ChunkFileTest {

    @Test
    public void testChunk() throws IOException {
        File sourceFile=new File("C:\\Users\\LIJIAHAO\\Desktop\\file\\obs\\1.mp4");
        String chunkFilePath="C:\\Users\\LIJIAHAO\\Desktop\\file\\obs\\chunk\\";
        File chunkFolder=new File(chunkFilePath);
        if (!chunkFolder.exists()){
            chunkFolder.mkdirs();
        }
        //分块大小
        long chunkSize=1024*1024*5;
        //分块数量
        long chunkNum=(long)Math.ceil(sourceFile.length()*1.0/chunkSize);
        System.out.println("分块数量："+chunkNum);
        //缓冲区大小
        byte[] buffer=new byte[1024];
        RandomAccessFile raf_read=new RandomAccessFile(sourceFile,"r");
        //分块
        for (int i = 0; i < chunkNum; i++) {
            //创建分块文件
            File file=new File(chunkFilePath+i);
            if (file.exists()){
                file.delete();
            }
            boolean newFile=file.createNewFile();
            if (newFile){
                //向文件中写入数据
                RandomAccessFile raf_write=new RandomAccessFile(file,"rw");
                int len=-1;
                while((len=raf_read.read(buffer))!=-1){
                    raf_write.write(buffer,0,len);
                    if (file.length()>=chunkSize){
                        break;
                    }
                }
                raf_write.close();
                System.out.println("完成分块"+i);
            }
        }
        raf_read.close();
    }

    @Test
    public void testMerge() throws IOException {
        //分块文件目录
        File chunkFolder=new File("C:\\Users\\LIJIAHAO\\Desktop\\file\\obs\\chunk\\");
        //原始文件
        File sourceFile=new File("C:\\Users\\LIJIAHAO\\Desktop\\file\\obs\\1.mp4");
        //合并文件
        File mergeFile=new File("C:\\Users\\LIJIAHAO\\Desktop\\file\\obs\\merge1.mp4");
        if (mergeFile.exists()){
            mergeFile.delete();
        }
        //写文件
        RandomAccessFile raf_write=new RandomAccessFile(mergeFile,"rw");
        byte[] buffer=new byte[1024];
        File[] fileArray=chunkFolder.listFiles();
        Arrays.sort(fileArray, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName())-Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File file : fileArray) {
            RandomAccessFile raf_read=new RandomAccessFile(file,"r");
            int len=-1;
            while((len=raf_read.read(buffer))!=-1){
                raf_write.write(buffer,0,len);
            }
            raf_read.close();
        }
        raf_write.close();

        //校验文件
        try(
                FileInputStream sourceInputStream=new FileInputStream(sourceFile);
                FileInputStream mergeInputStream=new FileInputStream(mergeFile);
                ){
            String sourceMd5= DigestUtils.md5Hex(sourceInputStream);
            String mergeMd5=DigestUtils.md5Hex(mergeInputStream);
            if (sourceMd5.equals(mergeMd5)){
                System.out.println("合并文件成功");
            }else {
                System.out.println("合并文件失败");
            }
        }
    }


}
