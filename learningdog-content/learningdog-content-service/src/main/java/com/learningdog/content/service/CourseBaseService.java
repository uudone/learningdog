package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.content.model.dto.AddCourseDto;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.EditCourseDto;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.po.CourseBase;
import com.learningdog.content.po.CourseMarket;

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

    /**
     * @param courseId:
     * @return CourseBaseInfoDto
     * @author getjiajia
     * @description 根据课程id查询课程基本信息
     */
    CourseBaseInfoDto getCourseBaseById(Long courseId);

    /**
     * @param companyId:
     * @param dto:
     * @return CourseBaseInfoDto
     * @author getjiajia
     * @description 修改课程基本信息
     */
    CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDto dto);

    /**
     * @param courseMarketNew:
     * @return CourseMarket
     * @author getjiajia
     * @description 根据课程id新增或修改课程营销信息
     */
    CourseMarket saveCourseMarket(CourseMarket courseMarketNew);
    /**
     * @param courseBaseNew:
     * @return CourseBase
     * @author getjiajia
     * @description 保存课程基本信息
     */
    CourseBase saveCourseBase(CourseBase courseBaseNew);

    /**
     * @param companyId:
     * @param courseId:
     * @return void
     * @author getjiajia
     * @description 删除课程基本信息、课程营销信息、课程计划、课程计划关联信息、课程师资
     */
    void deleteCourse(Long companyId,Long courseId);

    /**
     * @param courseId:
     * @param auditStatus:
     * @return void
     * @author getjiajia
     * @description 更新课程审核信息
     */
    void updateAuditStatus(Long courseId, String auditStatus);

    /**
     * @param courseId:
     * @param publishStatus:
     * @return void
     * @author getjiajia
     * @description 更新课程发布状态
     */
    void updatePublishStatus(Long courseId,String publishStatus);
}
