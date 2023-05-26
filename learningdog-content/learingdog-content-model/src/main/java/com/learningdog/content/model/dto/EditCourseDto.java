package com.learningdog.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: getjiajia
 * @description: 修改课程信息dto
 * @version: 1.0
 */
@Data
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto{
    @NotNull(message = "修改课程id不能为空")
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
