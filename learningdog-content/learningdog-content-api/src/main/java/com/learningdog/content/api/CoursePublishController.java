package com.learningdog.content.api;

import com.learningdog.content.po.CoursePublish;
import com.learningdog.content.service.CoursePublishService;
import com.learningdog.content.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    @PreAuthorize("hasAuthority('lg_teachmanager_course_publish')")
    public void coursepublish(@PathVariable("courseId")Long courseId, HttpServletRequest request){
        Long companyId= SecurityUtils.getCompanyId();
        coursePublishService.publish(companyId,courseId, request.getHeader("authorization"));
    }
    @ApiOperation("课程下线")
    @ResponseBody
    @GetMapping("/courseoffline/{courseId}")
    @PreAuthorize("hasAuthority('lg_teachmanager_course_publish')")
    public void courseoffline(@PathVariable("courseId")Long courseId){
        Long companyId= SecurityUtils.getCompanyId();
        coursePublishService.offline(companyId,courseId);
    }

    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursePublish(@PathVariable("courseId") Long courseId){
        return coursePublishService.getById(courseId);
    }

}
