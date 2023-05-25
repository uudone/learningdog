package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.content.model.dto.AddCourseDto;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.model.po.CourseBase;

/**
 * <p>
 * 课程基本信息 服务类
 * </p>
 *
 * @author getjiajia
 *
 */
public interface CourseBaseService extends IService<CourseBase> {

    /**
     * @param pageParams: 分页参数
     * @param queryCourseParamsDto: 查询条件
     * @return PageResult<CourseBase>
     * @author getjiajia
     * @description 课程查询接口
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * @param companyId: 机构Id
     * @param addCourseDto:  新增课程dto
     * @return CourseBaseInfoDto
     * @author getjiajia
     * @description 添加课程基本信息
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);
}
