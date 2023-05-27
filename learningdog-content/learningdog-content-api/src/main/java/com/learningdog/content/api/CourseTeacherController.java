package com.learningdog.content.api;

import com.learningdog.content.model.dto.AddCourseTeacherDto;
import com.learningdog.content.model.dto.EditCourseTeacherDto;
import com.learningdog.content.model.po.CourseTeacher;
import com.learningdog.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CourseTeacher addOrUpdateTeacher(@RequestBody @Validated EditCourseTeacherDto editCourseTeacherDto){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId=1232141425L;
        return courseTeacherService.insertOrUpdateTeacher(companyId,editCourseTeacherDto);
    }

    @ApiOperation("删除教师")
    @DeleteMapping("/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable("courseId")Long courseId,@PathVariable("teacherId")Long teacherId){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId=1232141425L;
        courseTeacherService.deleteTeacher(companyId,courseId,teacherId);
    }

}
