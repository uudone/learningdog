package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.model.dto.CourseCategoryTreeDto;
import com.learningdog.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 服务类
 * </p>
 *
 * @author getjiajia
 */
public interface CourseCategoryService extends IService<CourseCategory> {
    /**
     * @param id:  父节点id
     * @return List<CourseCategoryTreeDto>
     * @author getjiajia
     * @description 课程分类树形结构查询
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
