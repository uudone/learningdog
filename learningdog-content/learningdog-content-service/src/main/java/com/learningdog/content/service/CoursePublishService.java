package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.model.dto.CoursePublishDto;
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
     * @param token:
     * @return void
     * @author getjiajia
     * @description 课程发布
     */
    void publish(Long companyId,Long courseId,String token);

    /**
     * @param courseId:
     * @param companyId:
     * @param token:
     * @return void
     * @author getjiajia
     * @description 保存消息表记录
     */
    void saveCoursePublishMessage(Long courseId,Long companyId,String token);

    /**
     * @param courseId:
     * @return File
     * @author getjiajia
     * @description 生成课程静态化页面
     */
    File generateCourseHtml(long courseId);

    /**
     * @param companyId:
     * @param courseId:
     * @param file:
     * @param token:
     * @return void
     * @author getjiajia
     * @description 上传课程静态化页面
     */
    void uploadCourseHtml(long companyId,long courseId,File file,String token);

    /**
     * @param companyId:
     * @param courseId:
     * @return void
     * @author getjiajia
     * @description 课程下线
     */
    void offline(Long companyId, Long courseId);

    /**
     * @param courseId:
     * @return CoursePublishDto
     * @author getjiajia
     * @description 获取课程发布的所有信息
     */
    CoursePublishDto getCoursePublishInfo(Long courseId);

    /**
     * @param courseId:
     * @return CoursePublish
     * @author getjiajia
     * @description 从缓存中查询课程发布信息
     */
    CoursePublish getCoursePublishFromCache(Long courseId);

    /**
     * @param courseId:
     * @return void
     * @author getjiajia
     * @description 将新增的课程id添加到布隆过滤器
     */
    void addCourseIdToBloomFilter(Long courseId);
}
