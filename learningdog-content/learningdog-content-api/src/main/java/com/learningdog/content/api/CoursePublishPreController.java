package com.learningdog.content.api;

import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.service.CoursePublishPreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@Controller
public class CoursePublishPreController {

    @Resource
    private CoursePublishPreService  coursePublishPreService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId")Long courseId){
        ModelAndView modelAndView=new ModelAndView();
        CoursePreviewDto model=coursePublishPreService.getCoursePreviewInfo(courseId);
        modelAndView.addObject("model",model);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }
}
