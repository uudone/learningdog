package com.learningdog.media.jobhandler;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: getjiajia
 * @description: 分片任务
 * @version: 1.0
 */
@Component
@Slf4j
public class ShardingJob {

    @XxlJob("shardingJob")
    public void sharingJob(){
        int shardIndex= XxlJobHelper.getShardIndex();
        int shardTotal=XxlJobHelper.getShardTotal();
        log.info("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);
        log.info("开始执行第"+shardIndex+"批任务");
    }
}
