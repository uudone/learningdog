package com.learningdog.content.service;

import com.learningdog.base.model.PageParams;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: CourseBaseService测试类
 * @version: 1.0
 */
@SpringBootTest
public class CourseBaseServiceTest {

    @Resource
    private CourseBaseService courseBaseService;

    @Test
    public void testQueryCourseBaseList(){
        //设置查询条件
        QueryCourseParamsDto queryCourseParamsDto=new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203001");
        //分页参数
        PageParams pageParams=new PageParams(1L,2L);
        System.out.println(courseBaseService.queryCourseBaseList(pageParams, queryCourseParamsDto));
    }
}
