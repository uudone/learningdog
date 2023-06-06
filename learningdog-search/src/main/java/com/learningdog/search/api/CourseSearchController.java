package com.learningdog.search.api;

import com.learningdog.base.model.PageParams;
import com.learningdog.search.dto.SearchCourseParamDto;
import com.learningdog.search.dto.SearchPageResultDto;
import com.learningdog.search.po.CourseIndex;
import com.learningdog.search.service.SearchCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 课程搜索接口
 * @version: 1.0
 */
@Api(value = "课程搜索接口",tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

    @Resource
    SearchCourseService searchCourseService;

    @ApiOperation("课程搜索列表")
    @GetMapping("/list")
    public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto){
        return searchCourseService.queryCoursePublishIndex(pageParams,searchCourseParamDto);
    }

}
