package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.po.CoursePublish;

import java.io.File;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-05-24
 */
public interface CoursePublishService extends IService<CoursePublish> {

    /**
     * @param companyId:
     * @param courseId:
     * @return void
     * @author getjiajia
     * @description 课程发布
     */
    void publish(Long companyId,Long courseId);

    /**
     * @param courseId:
     * @return void
     * @author getjiajia
     * @description 保存消息表记录
     */
    void saveCoursePublishMessage(Long courseId);

    /**
     * @param courseId:
     * @return File
     * @author getjiajia
     * @description 生成课程静态化页面
     */
    File generateCourseHtml(long courseId);

    /**
     * @param file:
     * @return void
     * @author getjiajia
     * @description 上传课程静态化页面
     */
    void uploadCourseHtml(long courseId,File file);
}
