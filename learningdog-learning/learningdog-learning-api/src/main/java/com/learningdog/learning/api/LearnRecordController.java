package com.learningdog.learning.api;

import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.model.RestResponse;
import com.learningdog.learning.service.LearnRecordService;
import com.learningdog.learning.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api(value = "学习过程管理接口", tags = "学习过程管理接口")
public class LearnRecordController {

    @Resource
    private LearnRecordService  learnRecordService;

    @ApiOperation("获取视频")
    @GetMapping("/open/learn/getvideo/{courseId}/{teachplanId}/{mediaId}")
    public RestResponse<String> getVideo(@PathVariable("courseId")Long courseId,
                                         @PathVariable("teachplanId")Long teachplanId,
                                         @PathVariable("mediaId")String mediaId){
        String userId= null;
        try{
            userId= SecurityUtils.getUserId();
        }catch (LearningdogException e){
            //未登录用户操作
            return learnRecordService.getVideoFree(courseId,teachplanId,mediaId);
        }
        //已登录用户操作
        return learnRecordService.getVideo(userId,courseId,teachplanId,mediaId);
    }

}
