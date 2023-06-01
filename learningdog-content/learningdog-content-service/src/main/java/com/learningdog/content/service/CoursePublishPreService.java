package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.po.CoursePublishPre;

/**
 * <p>
 * 课程发布 服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-05-24
 */
public interface CoursePublishPreService extends IService<CoursePublishPre> {

    /**
     * @param courseId:
     * @return CoursePreviewDto
     * @author getjiajia
     * @description 获取课程预览信息
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);
}
