package com.learningdog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.content.mapper.CourseBaseMapper;
import com.learningdog.content.mapper.CourseTeacherMapper;
import com.learningdog.content.model.dto.EditCourseTeacherDto;
import com.learningdog.content.po.CourseBase;
import com.learningdog.content.po.CourseTeacher;
import com.learningdog.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Resource
    CourseTeacherService courseTeacherService;
    @Resource
    CourseBaseMapper courseBaseMapper;
    @Resource
    CourseTeacherMapper courseTeacherMapper;

    @Override
    @Transactional
    public CourseTeacher insertTeacher(Long companyId,EditCourseTeacherDto editCourseTeacherDto) {
        Long courseId=editCourseTeacherDto.getCourseId();
        //查询课程是否存在
        CourseBase courseBase = getCourseBase(courseId);
        //本机构只能添加本机构课程教师信息
        if (!courseBase.getCompanyId().equals(companyId)){
            LearningdogException.cast("本机构只能添加本机构课程教师信息");
        }
        CourseTeacher courseTeacher=new CourseTeacher();
        BeanUtils.copyProperties(editCourseTeacherDto,courseTeacher);
        courseTeacher.setCreateDate(LocalDateTime.now());
        int insert = courseTeacherMapper.insert(courseTeacher);
        if (insert<=0){
            LearningdogException.cast("新增课程教师失败");
        }
        return courseTeacher;
    }

    @Override
    public List<CourseTeacher> getCourseTeachers(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public CourseTeacher updateTeacher(Long companyId,EditCourseTeacherDto editCourseTeacherDto) {
        Long teacherId=editCourseTeacherDto.getId();
        //查询教师信息是否存在
        CourseTeacher courseTeacher = getCourseTeacher(teacherId);
        //查询课程是否存在
        CourseBase courseBase = getCourseBase(editCourseTeacherDto.getCourseId());
        //本机构只能修改本机构课程教师信息
        if (!courseBase.getCompanyId().equals(companyId)){
            LearningdogException.cast("本机构只能修改本机构课程教师信息");
        }
        //更新信息
        BeanUtils.copyProperties(editCourseTeacherDto,courseTeacher);
        courseTeacherMapper.updateById(courseTeacher);
        return courseTeacher;
    }



    @Override
    @Transactional
    public void deleteTeacher(Long companyId,Long courseId, Long teacherId) {
        //查询课程是否存在
        CourseBase courseBase = getCourseBase(courseId);
        //本机构只能删除本机构课程教师信息
        if (!courseBase.getCompanyId().equals(companyId)){
            LearningdogException.cast("本机构只能删除本机构课程教师信息");
        }
        //删除教师条件
        LambdaQueryWrapper<CourseTeacher> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getId,teacherId);
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        //删除
        int delete = courseTeacherMapper.delete(queryWrapper);
        if (delete<=0){
            LearningdogException.cast("删除失败");
        }
    }

    @Override
    @Transactional
    public CourseTeacher insertOrUpdateTeacher(Long companyId, EditCourseTeacherDto editCourseTeacherDto) {
        Long teacherId=editCourseTeacherDto.getId();
        if (teacherId==null){
            //新增课程教师
            courseTeacherService.insertTeacher(companyId,editCourseTeacherDto);
        }else{
            //修改课程教师
            courseTeacherService.updateTeacher(companyId,editCourseTeacherDto);
        }
        return null;
    }

    private CourseBase getCourseBase(Long courseId) {
        CourseBase courseBase=courseBaseMapper.selectById(courseId);
        if(courseBase==null){
            LearningdogException.cast("课程不存在");
        }
        return courseBase;
    }

    private CourseTeacher getCourseTeacher(Long teacherId) {
        CourseTeacher courseTeacher = courseTeacherMapper.selectById(teacherId);
        if(courseTeacher==null){
            LearningdogException.cast("课程教师不存在");
        }
        return courseTeacher;
    }


}
