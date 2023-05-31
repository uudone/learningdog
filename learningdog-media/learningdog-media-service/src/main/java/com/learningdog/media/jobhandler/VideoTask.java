package com.learningdog.media.jobhandler;

import com.learningdog.base.utils.Mp4VideoUtil;
import com.learningdog.media.model.po.MediaProcess;
import com.learningdog.media.service.MediaFilesService;
import com.learningdog.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author: getjiajia
 * @description: 视频处理任务
 * @version: 1.0
 */
@Slf4j
@Component
public class VideoTask {

    @Resource
    MediaFilesService mediaFilesService;

    @Resource
    MediaProcessService mediaProcessService;
    @Value("${videoprocess.ffmpegpath}")
    String ffmpegpath;

    /**
     * @param :
     * @return void
     * @author getjiajia
     * @description 定时被调度处理视频转码任务，视频统一转为mp4格式
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws InterruptedException {
        int shardIndex= XxlJobHelper.getShardIndex();
        int shardTotal=XxlJobHelper.getShardTotal();
        int processors=Runtime.getRuntime().availableProcessors();
        List<MediaProcess> mediaProcesses=mediaProcessService.getTaskList(shardIndex,shardTotal,processors);
        int size=mediaProcesses.size();
        log.debug("取出待处理视频任务{}条", size);
        if (size==0){
            return;
        }
        //启动size个线程的线程池
        ExecutorService threadPool= Executors.newFixedThreadPool(size);
        //计数器
        CountDownLatch countDownLatch=new CountDownLatch(size);
        //将任务加入到线程池
        mediaProcesses.forEach(mediaProcess -> {
            threadPool.execute(()->{
                File mp4File=null;
                File originFile=null;
                try{
                    long taskId=mediaProcess.getId();
                    boolean flag= mediaProcessService.startTask(taskId);
                    if (!flag){
                        return;
                    }
                    log.debug("开始执行任务：{}",mediaProcess);
                    String bucket=mediaProcess.getBucket();
                    String filePath=mediaProcess.getFilePath();
                    String fileId=mediaProcess.getFileId();
                    String filename=mediaProcess.getFilename();
                    //将要处理的文件下载到本地
                    originFile=mediaFilesService.downloadFileFromMinio(bucket,filePath);
                    if (originFile==null){
                        log.debug("下载待处理文件失败,originalFile:{}", bucket.concat(filePath));
                        mediaProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,"下载待处理文件失败");
                        return;
                    }
                    //创建转码后临时存储的文件
                    try{
                        mp4File=File.createTempFile("minio",".mp4");
                    }catch (IOException e){
                        log.error("创建mp4临时文件失败");
                        mediaProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,"创建mp4临时文件失败");
                        return;
                    }
                    //处理视频结果
                    String result="";
                    try{
                        Mp4VideoUtil mp4VideoUtil=new Mp4VideoUtil(ffmpegpath,originFile.getAbsolutePath(),mp4File.getName(),mp4File.getAbsolutePath());
                        //开始转换视频，成功返回success
                        result=mp4VideoUtil.generateMp4();
                    }catch (Exception e){
                        log.error("处理视频文件:{},出错:{}", mediaProcess.getFilePath(), e.getMessage());
                    }
                    //如果处理视频失败
                    if(!"success".equals(result)){
                        log.error("处理视频失败,视频地址:{},错误信息:{}", bucket + filePath, result);
                        mediaProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,result);
                        return;
                    }
                    //mp4在minio的存储路径
                    String objectName=getFilePath(fileId,".mp4");
                    //访问url
                    String url="/"+bucket+"/"+objectName;
                    try{
                        //将视频上传至minio
                        mediaFilesService.addMediaFileToMinio(mp4File.getAbsolutePath(),bucket,"video/mp4",objectName);
                        //将url存储至数据，并更新状态为成功，并将待处理视频记录删除存入历史
                        mediaProcessService.saveProcessFinishStatus(taskId,"2",fileId,url,null);
                        log.debug("视频转码成功，url:{}",url);
                    }catch (Exception e){
                        log.error("上传视频失败或入库失败,视频地址:{},错误信息:{}", bucket + objectName, e.getMessage());
                        mediaProcessService.saveProcessFinishStatus(taskId,"3",fileId,null,"处理后视频上传或入库失败");
                    }
                }finally {
                    countDownLatch.countDown();
                    if (mp4File!=null){
                        mp4File.delete();
                    }
                    if (originFile!=null){
                        originFile.delete();
                    }
                }
            });
        });
        //阻塞，等待所有任务完成，并给一个充裕的超时时间,防止无限等待，到达超时时间还没有处理完成则结束任务
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePath(String fileMd5,String fileExt){
        return   fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }

    /**
     * @param :
     * @return void
     * @author getjiajia
     * @description 处理任务表中正在运行的超时任务
     */
    @XxlJob("processTimeoutJob")
    public void processTimeoutJob(){
        int count= mediaProcessService.updateProcessTimeoutJob();
        log.debug("任务表中有{}个超时运行的任务被处理",count);
    }

}
