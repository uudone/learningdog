package com.learningdog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.CourseAuditStatus;
import com.learningdog.base.code.CoursePay;
import com.learningdog.base.code.CoursePublishStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.content.mapper.CourseBaseMapper;
import com.learningdog.content.mapper.CourseCategoryMapper;
import com.learningdog.content.mapper.CourseMarketMapper;
import com.learningdog.content.model.dto.AddCourseDto;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.model.po.CourseBase;
import com.learningdog.content.model.po.CourseCategory;
import com.learningdog.content.model.po.CourseMarket;
import com.learningdog.content.service.CourseBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 * 课程基本信息 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class CourseBaseServiceImpl extends ServiceImpl<CourseBaseMapper, CourseBase> implements CourseBaseService {

    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseMarketMapper courseMarketMapper;

    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper=new LambdaQueryWrapper<>();
        //设置查询参数
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()),CourseBase::getName,queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()),CourseBase::getStatus,queryCourseParamsDto.getPublishStatus());
        //设置分页参数
        Page<CourseBase> page=new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //封装结果
        PageResult<CourseBase> courseBasePageResult=new PageResult<>(pageResult.getRecords(),
                pageResult.getTotal(),pageParams.getPageNo(),pageParams.getPageSize());
        return courseBasePageResult;
    }
    /**
     * @param companyId:
     * @param dto:
     * @return CourseBaseInfoDto
     * @author getjiajia
     * @description 新增课程信息
     */
    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //向课程基本信息表保存信息
        CourseBase courseBaseNew = saveCourseBase(companyId, dto);
        Long courseId=courseBaseNew.getId();
        CourseMarket courseMarketNew= saveCourseMarket(dto, courseId);
        CourseBaseInfoDto courseBaseInfoDto=new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseMarketNew,courseBaseInfoDto);
        BeanUtils.copyProperties(courseBaseNew,courseBaseInfoDto);
        //还需要从course_category表中获取大小分类的名称
        String[] mt_st= getMtAndStName(dto.getMt(),dto.getSt());
        courseBaseInfoDto.setMtName(mt_st[0]);
        courseBaseInfoDto.setStName(mt_st[1]);
        //查询课程基本信息及营销信息并返回
        return courseBaseInfoDto;
    }
    /**
     * @param mt:
     * @param st:
     * @return String[2]
     * @author getjiajia
     * @description 根据大小分类id获取大小分类名称
     */
    private String[] getMtAndStName(String mt,String st) {
        if (StringUtils.isBlank(mt)) {
            throw new RuntimeException("课程分类为空");
        }

        if (StringUtils.isBlank(st)) {
            LearningdogException.cast("课程分类为空");
        }
        CourseCategory courseCategoryByMt=courseCategoryMapper.selectById(mt);
        CourseCategory courseCategoryBySt=courseCategoryMapper.selectById(st);
        String[] mt_st={courseCategoryByMt.getName(),courseCategoryBySt.getName()};
        return mt_st;
    }

    /**
     * @param dto:
     * @param courseId:
     * @return CourseMarket
     * @author getjiajia
     * @description 根据课程id新增或修改课程营销信息
     */
    @Transactional
    public CourseMarket saveCourseMarket(AddCourseDto dto, Long courseId) {
        //向课程营销表保存课程营销信息
        //合法性校验
        String charge= dto.getCharge();
        if(StringUtils.isBlank(charge)){
            LearningdogException.cast("收费规则没有选择");
        }
        if(CoursePay.CHARGE.equals(charge)){
            if(dto.getPrice()==null|| dto.getPrice().floatValue()<=0){
                LearningdogException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        CourseMarket courseMarketNew=new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        //查询是否否存在营销记录
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //如果不存在
        if(courseMarket==null){
            //新增营销记录
            int insert=courseMarketMapper.insert(courseMarketNew);
            if(insert<=0){
                LearningdogException.cast("保存课程营销信息失败");
            }
        }else {
            //修改营销记录
            int update=courseMarketMapper.updateById(courseMarketNew);
            if (update<=0){
                LearningdogException.cast("修改课程营销信息失败");
            }
        }
        return courseMarketNew;
    }

    /**
     * @param companyId:
     * @param dto:
     * @return CourseBase
     * @author getjiajia
     * @description 保存课程基本信息
     */
    @Transactional
    public CourseBase saveCourseBase(Long companyId, AddCourseDto dto) {
        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            LearningdogException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            LearningdogException.cast("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            LearningdogException.cast("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            LearningdogException.cast("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            LearningdogException.cast("收费规则为空");
        }
        //新增对象
        CourseBase courseBaseNew=new CourseBase();
        //将填写的课程信息赋值给新增对象
        BeanUtils.copyProperties(dto,courseBaseNew);
        //设置审核状态
        courseBaseNew.setAuditStatus(CourseAuditStatus.UN_SUBMITTED);
        //设置发布状态
        courseBaseNew.setStatus(CoursePublishStatus.UNPUBLISHED);
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程信息表
        int insert=courseBaseMapper.insert(courseBaseNew);
        if (insert<0){
            LearningdogException.cast("新增课程基本信息失败");
        }
        return courseBaseNew;
    }
}
