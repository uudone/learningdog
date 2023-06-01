package com.learningdog.content.api;

import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.service.CourseBaseService;
import com.learningdog.content.service.CoursePublishPreService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 课程访问公开查询接口
 * @version: 1.0
 */
@Api(value = "课程公开查询接口",tags = "课程公开查询接口")
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Resource
    CourseBaseService courseBaseService;
    @Resource
    CoursePublishPreService coursePublishPreService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId")Long courseId){
        return coursePublishPreService.getCoursePreviewInfo(courseId);
    }

}
