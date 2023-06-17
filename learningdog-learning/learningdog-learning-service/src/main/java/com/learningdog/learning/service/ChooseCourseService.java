package com.learningdog.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.po.CoursePublish;
import com.learningdog.learning.model.dto.ChooseCourseDto;
import com.learningdog.learning.po.ChooseCourse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-06-12
 */
public interface ChooseCourseService extends IService<ChooseCourse> {

    /**
     * @param userId:
     * @param courseId:
     * @return ChooseCourseDto
     * @author getjiajia
     * @description 添加选课记录
     */
    ChooseCourseDto addChooseCourse(String userId, Long courseId);

    /**
     * @param userId:
     * @param coursePublish:
     * @return ChooseCourse
     * @author getjiajia
     * @description 添加免费课程,免费课程加入选课记录表、我的课程表
     */
    ChooseCourse addFreeCourse(String userId, CoursePublish coursePublish);

    /**
     * @param userId:
     * @param coursePublish:
     * @return ChooseCourse
     * @author getjiajia
     * @description 添加收费课程
     */
    ChooseCourse addChargeCourse(String userId,CoursePublish coursePublish);


    /**
     * @param chooseCourseId:
     * @return boolean
     * @author getjiajia
     * @description 完成支付收费课程
     */
    boolean finishPayChargeCourse(String chooseCourseId);

}
