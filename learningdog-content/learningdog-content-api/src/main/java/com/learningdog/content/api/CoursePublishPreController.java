package com.learningdog.content.api;

import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.service.CoursePublishPreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * <p>
 * 课程发布 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Api("课程预发布接口")
@Controller
public class CoursePublishPreController {

    @Resource
    private CoursePublishPreService  coursePublishPreService;

    @ApiOperation("查看课程预发布信息")
    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId")Long courseId){
        ModelAndView modelAndView=new ModelAndView();
        CoursePreviewDto model=coursePublishPreService.getCoursePreviewInfo(courseId);
        modelAndView.addObject("model",model);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ApiOperation("提交课程审核")
    @PostMapping("/courseaudit/commit/{courseId}")
    @ResponseBody
    public void commitAudit(@PathVariable("courseId")Long courseId){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        coursePublishPreService.commitAudit(companyId,courseId);
    }
}
