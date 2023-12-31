package com.learningdog.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: getjiajia
 * @description: 课程计划绑定媒资文件dto
 * @version: 1.0
 */
@Data
@ApiModel(value="BindTeachplanMediaDto", description="教学计划-媒资绑定提交数据")
public class BindTeachplanMediaDto {

    @ApiModelProperty(value = "媒资文件id", required = true)
    @NotBlank(message = "文件id不能为空")
    private String mediaId;

    @ApiModelProperty(value = "媒资文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    @ApiModelProperty(value = "课程计划标识", required = true)
    @NotNull(message = "课程id不能为空")
    private Long teachplanId;

}
