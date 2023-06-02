package com.learningdog.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.CourseAuditStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.content.mapper.*;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.po.*;
import com.learningdog.content.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class CoursePublishPreServiceImpl extends ServiceImpl<CoursePublishPreMapper, CoursePublishPre> implements CoursePublishPreService {

    @Resource
    CourseBaseService courseBaseService;
    @Resource
    CourseTeacherService courseTeacherService;
    @Resource
    TeachplanMapper teachplanMapper;
    @Resource
    CourseMarketService courseMarketService;
    @Resource
    CoursePublishPreMapper coursePublishPreMapper;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //获取课程基本信息和营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseById(courseId);
        //获取课程计划信息
        List<TeachplanTreeDto> treeNodes = teachplanMapper.selectTreeNodes(courseId);
        //获取课程教师信息
        List<CourseTeacher> courseTeachers = courseTeacherService.getCourseTeachers(courseId);

        //封装返回信息
        CoursePreviewDto coursePreviewDto=new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(treeNodes);
        coursePreviewDto.setTeachers(courseTeachers);
        return coursePreviewDto;
    }

    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        //查询课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseById(courseId);
        if (courseBaseInfo==null){
            LearningdogException.cast("课程不存在");
        }
        //只允许机构提交本机构的课程信息
        if (!courseBaseInfo.getCompanyId().equals(companyId)){
            LearningdogException.cast("只允许提交本机构的课程");
        }
        //不允许已提交的课程再次提交
        if(CourseAuditStatus.SUBMITTED.equals(courseBaseInfo.getAuditStatus())){
            LearningdogException.cast("该课程已提交，正在审核");
        }
        //未上传图片的课程不允许提交
        if(StringUtils.isEmpty(courseBaseInfo.getPic())){
            LearningdogException.cast("提交失败，请上传课程图片");
        }
        //封装课程基本信息
        CoursePublishPre coursePublishPre=new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //查询课程营销信息
        CourseMarket courseMarket = courseMarketService.getById(courseId);
        if (courseMarket==null){
            LearningdogException.cast("课程营销信息不能为空");
        }
        String marketString = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(marketString);
        //查询课程计划
        List<TeachplanTreeDto> teachplans=teachplanMapper.selectTreeNodes(courseId);
        if (teachplans==null||teachplans.size()==0){
            LearningdogException.cast("课程计划不能为空");
        }
        String teachplanString=JSON.toJSONString(teachplans);
        coursePublishPre.setTeachplan(teachplanString);
        //查询课程教师信息
        List<CourseTeacher> courseTeachers = courseTeacherService.getCourseTeachers(courseId);
        if (courseTeachers==null||courseTeachers.size()==0){
            LearningdogException.cast("课程教师不能为空");
        }
        String teacherString=JSON.toJSONString(courseTeachers);
        coursePublishPre.setTeachers(teacherString);
        //插入到课程预发布表中
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        //设置课程审核状态
        coursePublishPre.setStatus(CourseAuditStatus.SUBMITTED);
        CoursePublishPre coursePublishPreFromDB=coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreFromDB==null){
            int insert=coursePublishPreMapper.insert(coursePublishPre);
            if (insert<=0){
                log.error("插入到CoursePublishPre表失败，id={}",courseId);
                LearningdogException.cast("提交失败");
            }
        }else {
            int update=coursePublishPreMapper.updateById(coursePublishPre);
            if (update<=0){
                log.error("更新CoursePublishPre表失败，id={}",courseId);
                LearningdogException.cast("提交失败");
            }
        }
        //更新课程基本表的审核状态
        courseBaseService.updateAuditStatus(courseId,CourseAuditStatus.SUBMITTED);
    }
}
