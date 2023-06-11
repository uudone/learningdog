package com.learningdog.content.api;

import com.learningdog.auth.po.User;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.base.utils.StringUtil;
import com.learningdog.content.model.dto.AddCourseDto;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.EditCourseDto;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.po.CourseBase;
import com.learningdog.content.service.CourseBaseService;
import com.learningdog.content.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 课程基本信息 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api("课程基本信息接口")
@RequestMapping("/course")
public class CourseBaseController {
    @Resource
    private CourseBaseService  courseBaseService;

    @ApiOperation("课程查询接口")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('lg_teachmanager_course_list')")
    public PageResult<CourseBase> list(PageParams pageParams,
                                       @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        Long companyId = SecurityUtils.getCompanyId();
        PageResult<CourseBase> result=courseBaseService.queryCourseBaseList(companyId,pageParams,queryCourseParamsDto);
        return result;
    }


    @ApiOperation("新增课程基本信息")
    @PostMapping
    @PreAuthorize("hasAuthority('lg_teachmanager_course_add')")
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated() AddCourseDto addCourseDto){
        Long companyId=SecurityUtils.getCompanyId();
        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }

    @ApiOperation("根据课程id查询课程基本信息")
    @GetMapping("/{courseId}")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable("courseId") Long courseId){
        return courseBaseService.getCourseBaseById(courseId);
    }

    @ApiOperation("修改课程基本信息")
    @PutMapping
    @PreAuthorize("hasAuthority('lg_teachmanager_course')")
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        Long companyId = SecurityUtils.getCompanyId();
        return courseBaseService.updateCourseBaseInfo(companyId,editCourseDto);
    }

    @ApiOperation("删除课程信息")
    @DeleteMapping("{courseId}")
    @PreAuthorize("hasAuthority('lg_teachmanager_course_del')")
    public void deleteCourse(@PathVariable("courseId")Long courseId){
        Long companyId = SecurityUtils.getCompanyId();
        courseBaseService.deleteCourse(companyId,courseId);
    }


}
