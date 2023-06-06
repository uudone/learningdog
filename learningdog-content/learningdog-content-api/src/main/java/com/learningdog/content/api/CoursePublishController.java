package com.learningdog.content.api;

import com.learningdog.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * <p>
 * 课程发布 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Api(value = "课程预览发布接口",tags = "课程预览发布接口")
@Controller
public class CoursePublishController {

    @Resource
    private CoursePublishService  coursePublishService;

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void coursepublish(@PathVariable("courseId")Long courseId){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId=1232141425L;
        coursePublishService.publish(companyId,courseId);
    }
    @ApiOperation("课程下线")
    @ResponseBody
    @GetMapping("/courseoffline/{courseId}")
    public void courseoffline(@PathVariable("courseId")Long courseId){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId=1232141425L;
        coursePublishService.offline(companyId,courseId);
    }

}
