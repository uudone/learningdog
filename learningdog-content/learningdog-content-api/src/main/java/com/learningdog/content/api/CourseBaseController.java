package com.learningdog.content.api;

import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.content.model.dto.AddCourseDto;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.EditCourseDto;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.po.CourseBase;
import com.learningdog.content.service.CourseBaseService;
import com.learningdog.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public PageResult<CourseBase> list(PageParams pageParams,
                                       @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto){
        PageResult<CourseBase> result=courseBaseService.queryCourseBaseList(pageParams,queryCourseParamsDto);
        return result;
    }

    @ApiOperation("新增课程基本信息")
    @PostMapping
    public CourseBaseInfoDto createCourseBase(@RequestBody @Validated() AddCourseDto addCourseDto){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId=1232141425L;
        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }

    @ApiOperation("根据课程id查询课程基本信息")
    @GetMapping("/{courseId}")
    @ApiImplicitParam(value = "courseId",name = "课程Id",required = true,dataType = "Long",paramType = "path")
    public CourseBaseInfoDto getCourseBaseById(@PathVariable("courseId") Long courseId){
        //取出用户当前身份

        System.out.println(SecurityUtils.getUser());

        return courseBaseService.getCourseBaseById(courseId);
    }

    @ApiOperation("修改课程基本信息")
    @PutMapping
    public CourseBaseInfoDto modifyCourseBase(@RequestBody @Validated EditCourseDto editCourseDto){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseBaseService.updateCourseBaseInfo(companyId,editCourseDto);
    }

    @ApiOperation("删除课程信息")
    @DeleteMapping("{courseId}")
    public void deleteCourse(@PathVariable("courseId")Long courseId){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        courseBaseService.deleteCourse(companyId,courseId);
    }

}
