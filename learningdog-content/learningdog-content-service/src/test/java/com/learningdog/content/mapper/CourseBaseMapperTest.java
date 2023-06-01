package com.learningdog.content.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: getjiajia
 * @description: 测试CourseBaseMapper中的方法
 * @version: 1.0
 */
@SpringBootTest
public class CourseBaseMapperTest {

    @Resource
    private CourseBaseMapper courseBaseMapper;


    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase=courseBaseMapper.selectById(74L);
        Assertions.assertNotNull(courseBase);
        LambdaQueryWrapper<CourseBase> queryWrapper=new LambdaQueryWrapper<>();
        //设置查询条件
        QueryCourseParamsDto queryCourseParamsDto=new QueryCourseParamsDto();
        queryCourseParamsDto.setCourseName("java");
        queryCourseParamsDto.setAuditStatus("202004");
        queryCourseParamsDto.setPublishStatus("203001");
        //拼接查询条件
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        //分页参数
        PageParams pageParams=new PageParams(1L,2L);
        Page<CourseBase> page=new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        Page<CourseBase> pageResult=courseBaseMapper.selectPage(page,queryWrapper);
        //封装数据
        PageResult<CourseBase> courseBasePageResult=new PageResult<>(pageResult.getRecords(),pageResult.getTotal(),pageParams.getPageNo(),pageParams.getPageSize());
        System.out.println(courseBasePageResult);
    }
}
