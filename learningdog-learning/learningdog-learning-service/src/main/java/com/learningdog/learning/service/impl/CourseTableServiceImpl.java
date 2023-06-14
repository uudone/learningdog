package com.learningdog.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.ChooseCoursePermission;
import com.learningdog.base.code.ChooseCourseStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.learning.mapper.ChooseCourseMapper;
import com.learningdog.learning.mapper.CourseTableMapper;
import com.learningdog.learning.model.dto.CourseTableDto;
import com.learningdog.learning.po.ChooseCourse;
import com.learningdog.learning.po.CourseTable;
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
public class CourseTableServiceImpl extends ServiceImpl<CourseTableMapper, CourseTable> implements CourseTableService {

    @Resource
    CourseTableMapper courseTableMapper;
    @Resource
    ChooseCourseMapper chooseCourseMapper;

    @Override
    @Transactional
    public CourseTable addCourseTable(Long chooseCourseId) {
        ChooseCourse chooseCourse=chooseCourseMapper.selectById(chooseCourseId);
        if (chooseCourse==null){
            log.debug("选课失败，选课记录不存在,chooseCourseId:{}",chooseCourseId);
            LearningdogException.cast("选课失败，选课记录不存在");
        }
        //选课记录表中选课成功而且未过期才能添加到我的课程表中
        if(!ChooseCourseStatus.SUCCESS.equals(chooseCourse.getStatus())){
            LearningdogException.cast("选课未成功，无法添加到课程表");
        }
        //查询我的课程表
        CourseTable courseTable = courseTableMapper.selectOne(new LambdaQueryWrapper<CourseTable>()
                .eq(CourseTable::getUserId, chooseCourse.getUserId())
                .eq(CourseTable::getCourseId,chooseCourse.getCourseId())
        );
        CourseTable courseTableNew=new CourseTable();
        copyToCourseTable(chooseCourse, courseTableNew);
        if (courseTable==null){
            //如果我的课程中没有该课程，插入
            courseTableNew.setCreateDate(LocalDateTime.now());
            int insert = courseTableMapper.insert(courseTableNew);
            if (insert<=0){
                log.debug("插入我的课程表失败,chooseCourse:{}",chooseCourse);
                LearningdogException.cast("选课失败,请重试");
            }
        }else {
            //如果我的课程中有该课程了，更新
            courseTableNew.setUpdateDate(LocalDateTime.now());
            courseTableNew.setId(courseTable.getId());
            int update = courseTableMapper.updateById(courseTableNew);
            if (update<=0){
                log.debug("更新我的课程表失败,chooseCourse:{}",chooseCourse);
                LearningdogException.cast("选课失败,请重试");
            }
        }
        return courseTableNew;
    }

    @Override
    public CourseTableDto getLearningStatus(String userId, Long courseId) {
        //查询我的课程表
        CourseTable courseTable = courseTableMapper.selectOne(new LambdaQueryWrapper<CourseTable>()
                .eq(CourseTable::getUserId, userId)
                .eq(CourseTable::getCourseId,courseId)
        );
        CourseTableDto courseTableDto=new CourseTableDto();
        if (courseTable==null){
            //没有选课或选课后没有支付
            courseTableDto.setLearnStatus(ChooseCoursePermission.UNPAID_OR_UNSELECT);
            return courseTableDto;
        }
        BeanUtils.copyProperties(courseTable,courseTableDto);
        //查询课程是否过期
        boolean isExpire=courseTable.getValidtimeEnd().isBefore(LocalDateTime.now());
        //如果过期
        if (isExpire){
            courseTableDto.setLearnStatus(ChooseCoursePermission.EXPIRED);
            return courseTableDto;
        }
        //如果没有过期
        courseTableDto.setLearnStatus(ChooseCoursePermission.TO_LEARN);
        return courseTableDto;
    }

    /**
     * @param chooseCourse:
     * @param courseTableNew:
     * @return CourseTable
     * @author getjiajia
     * @description 封装课程表信息
     */
    private static CourseTable copyToCourseTable(ChooseCourse chooseCourse, CourseTable courseTableNew) {
        courseTableNew.setChooseCourseId(chooseCourse.getId());
        courseTableNew.setUserId(chooseCourse.getUserId());
        courseTableNew.setCourseId(chooseCourse.getCourseId());
        courseTableNew.setCompanyId(chooseCourse.getCompanyId());
        courseTableNew.setCourseName(chooseCourse.getCourseName());
        courseTableNew.setCourseType(chooseCourse.getOrderType());
        courseTableNew.setValidtimeStart(LocalDateTime.now());
        courseTableNew.setValidtimeEnd(LocalDateTime.now().plusYears(1L));
        courseTableNew.setRemarks(chooseCourse.getRemarks());
        return courseTableNew;
    }
}
