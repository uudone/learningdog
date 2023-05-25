package com.learningdog.content.model.dto;

import com.learningdog.content.model.po.CourseCategory;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: getjiajia
 * @description: 课程分类树形节点
 * @version: 1.0
 */
@Data
@ToString
public class CourseCategoryTreeDto extends CourseCategory {
    private List<CourseCategoryTreeDto> childrenTreeNodes;
}
