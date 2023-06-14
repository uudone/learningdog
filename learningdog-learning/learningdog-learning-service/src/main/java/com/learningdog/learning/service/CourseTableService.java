package com.learningdog.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.learning.model.dto.CourseTableDto;
import com.learningdog.learning.po.ChooseCourse;
import com.learningdog.learning.po.CourseTable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-06-12
 */
public interface CourseTableService extends IService<CourseTable> {


    /**
     * @param chooseCourseId:
     * @return CourseTable
     * @author getjiajia
     * @description 添加到我的课程表
     */
    CourseTable addCourseTable(Long chooseCourseId);


    /**
     * @param userId:
     * @param courseId:
     * @return CourseTableDto
     * @author getjiajia
     * @description 获取学习的状态
     */
    CourseTableDto getLearningStatus(String userId,Long courseId);

}
