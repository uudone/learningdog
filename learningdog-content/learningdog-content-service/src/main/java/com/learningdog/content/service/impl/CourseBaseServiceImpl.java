package com.learningdog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.CourseAuditStatus;
import com.learningdog.base.code.CoursePay;
import com.learningdog.base.code.CoursePublishStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.model.PageParams;
import com.learningdog.base.model.PageResult;
import com.learningdog.content.mapper.*;
import com.learningdog.content.model.dto.AddCourseDto;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.EditCourseDto;
import com.learningdog.content.model.dto.QueryCourseParamsDto;
import com.learningdog.content.po.*;
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
    CourseBaseService courseBaseService;
    @Resource
    CourseBaseMapper courseBaseMapper;
    @Resource
    CourseMarketMapper courseMarketMapper;
    @Resource
    CourseCategoryMapper courseCategoryMapper;
    @Resource
    CourseTeacherMapper courseTeacherMapper;
    @Resource
    TeachplanMapper teachplanMapper;

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
        CourseBase courseBaseNew=new CourseBase();
        BeanUtils.copyProperties(dto,courseBaseNew);
        //机构id
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew = courseBaseService.saveCourseBase(courseBaseNew);
        //向课程营销表保存信息
        Long courseId=courseBaseNew.getId();
        CourseMarket courseMarketNew=new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarketNew);
        courseMarketNew.setId(courseId);
        courseMarketNew=courseBaseService.saveCourseMarket(courseMarketNew);
        //还需要从course_category表中获取大小分类的名称
        String[] mt_st= getMtAndStName(dto.getMt(),dto.getSt());
        //封装数据
        CourseBaseInfoDto courseBaseInfoDto = copyToCourseBaseInfo(courseBaseNew, courseMarketNew, mt_st);
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto getCourseBaseById(Long courseId) {
        //查询课程信息
        CourseBase courseBase=courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            return null;
        }
        //查询课程营销信息
        CourseMarket courseMarket=courseMarketMapper.selectById(courseId);
        //查询大小分类名称
        String[] mt_st = getMtAndStName(courseBase.getMt(), courseBase.getSt());
        //封装信息
        CourseBaseInfoDto courseBaseInfoDto=copyToCourseBaseInfo(courseBase,courseMarket,mt_st);
        return courseBaseInfoDto;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBaseInfo(Long companyId, EditCourseDto dto) {
        Long courseId=dto.getId();
        CourseBase courseBase=courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            LearningdogException.cast("课程不存在");
        }
        //校验本机构只能修改本机构的课程
        if(!courseBase.getCompanyId().equals(companyId)){
            LearningdogException.cast("本机构只能修改本机构的课程");
        }
        //更新课程基本信息
        LocalDateTime createDate = courseBase.getCreateDate();
        BeanUtils.copyProperties(dto,courseBase);
        courseBase.setCreateDate(createDate);
        courseBase.setChangeDate(LocalDateTime.now());
        int update=courseBaseMapper.updateById(courseBase);
        if(update<=0){
            LearningdogException.cast("课程信息更新失败");
        }
        //更新课程营销信息
        CourseMarket courseMarket=new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        courseMarket=courseBaseService.saveCourseMarket(courseMarket);
        //查询课程大小分类名称
        String[] mt_st = getMtAndStName(courseBase.getMt(), courseBase.getSt());
        //封装返回信息
        CourseBaseInfoDto courseBaseInfoDto = copyToCourseBaseInfo(courseBase, courseMarket,mt_st);
        return courseBaseInfoDto;
    }

    /**
     * @param courseBase:
     * @param courseMarket:
     * @param mt_st:
     * @return CourseBaseInfoDto
     * @author getjiajia
     * @description 封装CourseBaseInfoDto数据
     */
    private CourseBaseInfoDto copyToCourseBaseInfo(CourseBase courseBase, CourseMarket courseMarket,String[] mt_st) {
        CourseBaseInfoDto courseBaseInfoDto=new CourseBaseInfoDto();
        if (courseBase!=null){
            BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        }
        if (courseMarket!=null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }
        if (mt_st!=null){
            courseBaseInfoDto.setMtName(mt_st[0]);
            courseBaseInfoDto.setStName(mt_st[1]);
        }
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
        CourseCategory courseCategoryByMt=courseCategoryMapper.selectById(mt);
        CourseCategory courseCategoryBySt=courseCategoryMapper.selectById(st);
        String[] mt_st={courseCategoryByMt.getName(),courseCategoryBySt.getName()};
        return mt_st;
    }

    @Override
    @Transactional
    public CourseMarket saveCourseMarket(CourseMarket courseMarketNew) {
        Long courseId = courseMarketNew.getId();
        //向课程营销表保存课程营销信息
        String charge= courseMarketNew.getCharge();
        //合法性校验
        if(CoursePay.CHARGE.equals(charge)){
            if(courseMarketNew.getPrice()==null|| courseMarketNew.getPrice().floatValue()<=0){
                LearningdogException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
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

    @Override
    @Transactional
    public CourseBase saveCourseBase(CourseBase courseBaseNew) {

        //设置审核状态
        courseBaseNew.setAuditStatus(CourseAuditStatus.UN_SUBMITTED);
        //设置发布状态
        courseBaseNew.setStatus(CoursePublishStatus.UNPUBLISHED);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程信息表
        int insert=courseBaseMapper.insert(courseBaseNew);
        if (insert<0){
            LearningdogException.cast("新增课程基本信息失败");
        }
        return courseBaseNew;
    }

    @Override
    @Transactional
    public void deleteCourse(Long companyId,Long courseId) {
        //查询课程信息是否存在
        CourseBase courseBase=courseBaseMapper.selectById(courseId);
        if (courseBase==null){
            LearningdogException.cast("课程信息不存在");
        }
        //本机构只能删除本机构的课程
        if (!courseBase.getCompanyId().equals(companyId)){
            LearningdogException.cast("本机构只能删除本机构的课程");
        }
        //课程的审核状态为未提交时方可删除
        if (!courseBase.getAuditStatus().equals(CourseAuditStatus.UN_SUBMITTED)){
            LearningdogException.cast("课程的审核状态为未提交时方可删除");
        }
        //删除课程基本信息
        int delete=courseBaseMapper.deleteById(courseId);
        if (delete<=0){
            LearningdogException.cast("删除课程信息失败");
        }
        //删除课程营销信息
        courseMarketMapper.deleteById(courseId);
        //删除课程计划信息
        LambdaQueryWrapper<Teachplan> teachplanQuery=new LambdaQueryWrapper<>();
        teachplanQuery.eq(Teachplan::getCourseId,courseId);
        teachplanMapper.delete(teachplanQuery);
        //删除课程教师信息
        LambdaQueryWrapper<CourseTeacher> teacherQuery=new LambdaQueryWrapper<>();
        teacherQuery.eq(CourseTeacher::getCourseId,courseId);
        courseTeacherMapper.delete(teacherQuery);
    }

    @Override
    public void updateAuditStatus(Long courseId, String auditStatus) {
        int update=courseBaseMapper.update(null,new LambdaUpdateWrapper<CourseBase>()
                .eq(CourseBase::getId,courseId)
                .set(CourseBase::getAuditStatus,auditStatus));
        if (update<=0){
            LearningdogException.cast("更新课程状态失败");
        }
    }

    @Override
    public void updatePublishStatus(Long courseId, String publishStatus) {
        int update=courseBaseMapper.update(null,new LambdaUpdateWrapper<CourseBase>()
                .eq(CourseBase::getId,courseId)
                .set(CourseBase::getStatus,publishStatus));
        if (update<=0){
            LearningdogException.cast("更新课程发布状态失败");
        }
    }
}
