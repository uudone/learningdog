package com.learningdog.search.api;

import com.learningdog.base.exception.LearningdogException;
import com.learningdog.search.po.CourseIndex;
import com.learningdog.search.service.CourseDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 课程文档接口
 * @version: 1.0
 */
@Api(value = "课程信息文档接口", tags = "课程信息文档接口")
@RestController
@RequestMapping("/index")
public class CourseDocumentController {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;

    @Resource
    CourseDocumentService courseDocumentService;

    @ApiOperation("添加课程内容到文档中")
    @PostMapping("/course")
    @PreAuthorize("hasAuthority('lg_teachmanager_course_publish')")
    public Boolean add(@RequestBody CourseIndex courseIndex){
        Long courseId=courseIndex.getId();
        if (courseId==null){
            LearningdogException.cast("课程id为空");
        }
        Boolean result=courseDocumentService.addCourseDocument(courseIndexStore,String.valueOf(courseId),courseIndex);
        if(!result){
            LearningdogException.cast("添加课程文档失败");
        }
        return true;
    }

    @ApiOperation("更新课程文档")
    @PutMapping("/course")
    @PreAuthorize("hasAuthority('lg_teachmanager_course_publish')")
    public Boolean update(@RequestBody CourseIndex courseIndex){
        Long courseId=courseIndex.getId();
        if (courseId==null){
            LearningdogException.cast("课程id为空");
        }
        Boolean result=courseDocumentService.updateCourseDocument(courseIndexStore,String.valueOf(courseId),courseIndex);
        if(!result){
            LearningdogException.cast("更新课程文档失败");
        }
        return true;
    }

    @ApiOperation("删除课程文档")
    @DeleteMapping("/course/delete/{id}")
    @PreAuthorize("hasAuthority('lg_teachmanager_course_publish')")
    public Boolean delete(@PathVariable("id")Long id){
        Boolean result=courseDocumentService.deleteCourseDocument(courseIndexStore,String.valueOf(id));
        if(!result){
            LearningdogException.cast("删除课程文档失败");
        }
        return true;
    }


}
