package com.learningdog.content.api;

import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.content.model.dto.AddCourseDto;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.model.po.CourseBase;
import com.learningdog.content.service.CourseBaseService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CourseBaseInfoDto createCourseBase(@RequestBody AddCourseDto addCourseDto){
        //todo:机构id，由于认证系统没有上线暂时硬编码
        Long companyId=1232141425L;
        return courseBaseService.createCourseBase(companyId,addCourseDto);
    }

}