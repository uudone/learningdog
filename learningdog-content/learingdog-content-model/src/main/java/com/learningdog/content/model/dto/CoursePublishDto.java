package com.learningdog.content.model.dto;

import com.learningdog.content.po.CourseTeacher;
import lombok.Data;

import java.util.List;

/**
 * @author: getjiajia
 * @description: 课程发布dto
 * @version: 1.0
 */
@Data
public class CoursePublishDto {
    //包括课程基本信息和营销信息
    private CourseBaseInfoDto courseBase;
    //课程计划信息
    private List<TeachplanTreeDto> teachplans;
    //课程教师信息
    private List<CourseTeacher> teachers;
}
