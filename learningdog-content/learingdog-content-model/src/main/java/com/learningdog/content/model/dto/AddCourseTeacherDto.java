package com.learningdog.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: getjiajia
 * @description: 新增课程教师dto
 * @version: 1.0
 */
@Data
@ApiModel(value = "AddCourseTeacherDto",description = "新增课程教师基本信息")
public class AddCourseTeacherDto {

    @ApiModelProperty(value = "课程id",required = true)
    @NotNull(message = "课程id不能为空")
    private Long courseId;

    @ApiModelProperty(value = "教师名称",required = true)
    @NotBlank(message = "教师名称不能为空")
    private String teacherName;

    @ApiModelProperty(value = "教师职位",required = true)
    @NotBlank(message = "教师职位不能为空")
    private String position;

    @ApiModelProperty(value = "教师简介",required = true)
    @NotBlank(message = "教师简介不能为空")
    private String introduction;
}
