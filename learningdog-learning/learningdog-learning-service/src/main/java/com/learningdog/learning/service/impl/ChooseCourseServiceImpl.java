package com.learningdog.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.ChooseCourseStatus;
import com.learningdog.base.code.ChooseCourseType;
import com.learningdog.base.code.CoursePay;
import com.learningdog.base.code.CoursePublishStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.content.po.CoursePublish;
import com.learningdog.feign.client.ContentClient;
import com.learningdog.learning.mapper.ChooseCourseMapper;
import com.learningdog.learning.model.dto.ChooseCourseDto;
import com.learningdog.learning.model.dto.CourseTableDto;
import com.learningdog.learning.po.ChooseCourse;
import com.learningdog.learning.service.ChooseCourseService;
import com.learningdog.learning.service.CourseTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class ChooseCourseServiceImpl extends ServiceImpl<ChooseCourseMapper, ChooseCourse> implements ChooseCourseService {

    @Resource
    ContentClient contentClient;
    @Resource
    ChooseCourseService chooseCourseService;
    @Resource
    ChooseCourseMapper chooseCourseMapper;
    @Resource
    CourseTableService courseTableService;

    @Override
    @Transactional
    public ChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //根据课程id查询课程发布表中的信息
        CoursePublish coursePublish = contentClient.getCoursePublish(courseId);
        if (coursePublish==null){
            LearningdogException.cast("课程不存在");
        }
        //已发布课程才能选课
        if (!CoursePublishStatus.PUBLISHED.equals(coursePublish.getStatus())){
            LearningdogException.cast("该课程未发布");
        }
        //课程是否收费
        String charge = coursePublish.getCharge();
        ChooseCourse chooseCourse=null;
        if (CoursePay.FREE.equals(charge)){
            //添加免费课程
            chooseCourse= chooseCourseService.addFreeCourse(userId,coursePublish);
        }else {
            //添加收费课程
            chooseCourse=chooseCourseService.addChargeCourse(userId,coursePublish);
        }
        ChooseCourseDto chooseCourseDto=new ChooseCourseDto();
        BeanUtils.copyProperties(chooseCourse,chooseCourseDto);
        //获取学习资格
        CourseTableDto courseTableDto = courseTableService.getLearningStatus(userId, courseId);
        chooseCourseDto.setLearnStatus(courseTableDto.getLearnStatus());
        return chooseCourseDto;
    }

    @Override
    @Transactional
    public ChooseCourse addFreeCourse(String userId, CoursePublish coursePublish) {
        Long courseId=coursePublish.getId();
        //查询选课记录表
        ChooseCourse chooseCourse = chooseCourseMapper.selectOne(new LambdaQueryWrapper<ChooseCourse>()
                .eq(ChooseCourse::getCourseId, courseId)
                .eq(ChooseCourse::getUserId, userId)
                .eq(ChooseCourse::getStatus, ChooseCourseStatus.SUCCESS)
                .eq(ChooseCourse::getOrderType, ChooseCourseType.FREE)
        );
        ChooseCourse chooseCourseNew=new ChooseCourse();
        copyPublishFreeToChooseCourse(userId,coursePublish,chooseCourseNew);
        if (chooseCourse==null){
            //如果没有该课程的选课记录，插入
            int insert = chooseCourseMapper.insert(chooseCourseNew);
            if (insert<=0){
                log.debug("插入选课记录失败,该课程为免费,userId:{},coursePublish:{}",userId,coursePublish);
                LearningdogException.cast("选课失败,请重试");
            }
        }else {
            //如果有该课程的选课记录，更新
            chooseCourseNew.setId(chooseCourse.getId());
            int update = chooseCourseMapper.updateById(chooseCourseNew);
            if (update<=0){
                log.debug("更新选课记录失败,该课程为免费,userId:{},coursePublish:{}",userId,coursePublish);
                LearningdogException.cast("选课失败,请重试");
            }
        }
        //添加到我的课程表中
        courseTableService.addCourseTable(chooseCourseNew.getId());
        return chooseCourseNew;
    }



    @Override
    @Transactional
    public ChooseCourse addChargeCourse(String userId, CoursePublish coursePublish) {
        Long courseId=coursePublish.getId();
        ChooseCourse chooseCourse = chooseCourseMapper.selectOne(new LambdaQueryWrapper<ChooseCourse>()
                .eq(ChooseCourse::getCourseId, courseId)
                .eq(ChooseCourse::getUserId, userId)
                .eq(ChooseCourse::getStatus, ChooseCourseStatus.TO_PAY)
                .eq(ChooseCourse::getOrderType, ChooseCourseType.CHARGE));
        ChooseCourse chooseCourseNew=new ChooseCourse();
        copyPublishChargeToChooseCourse(userId,coursePublish,chooseCourseNew);
        if (chooseCourse==null){
            //如果没有该课程的选课记录，插入
            int insert = chooseCourseMapper.insert(chooseCourseNew);
            if (insert<=0){
                log.debug("插入选课记录失败,该课程为收费,userId:{},coursePublish:{}",userId,coursePublish);
                LearningdogException.cast("选课失败,请重试");
            }
        }else {
            //如果有该课程的选课记录，更新
            chooseCourseNew.setId(chooseCourse.getId());
            int update = chooseCourseMapper.updateById(chooseCourseNew);
            if (update<=0){
                log.debug("更新选课记录失败,该课程为收费,userId:{},coursePublish:{}",userId,coursePublish);
                LearningdogException.cast("选课失败,请重试");
            }
        }
        return chooseCourseNew;
    }

    /**
     * @param userId:
     * @param coursePublish:
     * @param chooseCourse:
     * @return ChooseCourse
     * @author getjiajia
     * @description 将收费课程信息封装到选课信息
     */
    private ChooseCourse copyPublishChargeToChooseCourse(String userId, CoursePublish coursePublish, ChooseCourse chooseCourse) {
        chooseCourse.setCourseId(coursePublish.getId());
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType(ChooseCourseType.CHARGE);
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursePublish.getPrice());
        chooseCourse.setValidDays(coursePublish.getValidDays());
        chooseCourse.setStatus(ChooseCourseStatus.TO_PAY);
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursePublish.getValidDays()));
        return chooseCourse;
    }

    /**
     * @param userId:
     * @param coursePublish:
     * @param chooseCourse:
     * @return ChooseCourse
     * @author getjiajia
     * @description 将免费课程信息封装到选课信息
     */
    private ChooseCourse copyPublishFreeToChooseCourse(String userId, CoursePublish coursePublish, ChooseCourse chooseCourse) {
        chooseCourse.setCourseId(coursePublish.getId());
        chooseCourse.setCourseName(coursePublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursePublish.getCompanyId());
        chooseCourse.setOrderType(ChooseCourseType.FREE);
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursePublish.getPrice());
        chooseCourse.setValidDays(365);//免费课程默认365
        chooseCourse.setStatus(ChooseCourseStatus.SUCCESS);
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(365));
        return chooseCourse;
    }
}
