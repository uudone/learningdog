package com.learningdog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learningdog.content.model.dto.CourseCategoryTreeDto;
import com.learningdog.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author getjiajia
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * @param id:
     * @return List<CourseCategoryTreeDto>
     * @author getjiajia
     * @description 课程分类树形结构查询
     */
    List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
