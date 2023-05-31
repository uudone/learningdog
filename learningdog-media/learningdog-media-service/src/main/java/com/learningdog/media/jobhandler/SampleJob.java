package com.learningdog.media.jobhandler;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: getjiajia
 * @description: 测试执行器
 * @version: 1.0
 */
@Component
@Slf4j
public class SampleJob {

    @XxlJob("sampleJob")
    public void sampleJob() throws Exception{
        log.info("sampleJob开始执行....");
    }

}
