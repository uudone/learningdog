package com.learningdog.content.api;

import com.learningdog.content.model.dto.EditCourseTeacherDto;
import com.learningdog.content.po.CourseTeacher;
import com.learningdog.content.service.CourseTeacherService;
import com.learningdog.content.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api("课程教师信息操作接口")
@RequestMapping("/courseTeacher")
public class CourseTeacherController {

    @Autowired
    private CourseTeacherService  courseTeacherService;

    @ApiOperation("查询课程所有教师")
    @GetMapping("/list/{courseId}")
    public List<CourseTeacher> getCourseTeachers(@PathVariable("courseId")Long courseId){
        return courseTeacherService.getCourseTeachers(courseId);
    }


    @ApiOperation("新增或修改课程教师")
    @PostMapping
    @PreAuthorize("hasAuthority('lg_teachmanager_course')")
    public CourseTeacher addOrUpdateTeacher(@RequestBody @Validated EditCourseTeacherDto editCourseTeacherDto){
        Long companyId= SecurityUtils.getCompanyId();
        return courseTeacherService.insertOrUpdateTeacher(companyId,editCourseTeacherDto);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/course/{courseId}/{teacherId}")
    @PreAuthorize("hasAuthority('lg_teachmanager_course')")
    public void deleteTeacher(@PathVariable("courseId")Long courseId,@PathVariable("teacherId")Long teacherId){
        Long companyId= SecurityUtils.getCompanyId();
        courseTeacherService.deleteTeacher(companyId,courseId,teacherId);
    }

}
