package com.learningdog.content.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: getjiajia
 * @description: 课程预览dto
 * @version: 1.0
 */
@Data
public class CoursePreviewDto {
    //包括课程基本信息和营销信息
    private CourseBaseInfoDto courseBase;
    //课程计划信息
    private List<TeachplanTreeDto> teachplans;

}
