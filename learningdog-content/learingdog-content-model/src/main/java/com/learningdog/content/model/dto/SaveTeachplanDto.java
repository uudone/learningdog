package com.learningdog.content.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author: getjiajia
 * @description: 保存课程计划dto
 * @version: 1.0
 */
@Data
public class SaveTeachplanDto {
    /***
     * 教学计划id
     */
    private Long id;

    /**
     * 课程计划名称
     */
    @NotBlank(message = "课程计划名称不能为空")
    private String pname;

    /**
     * 课程计划父级Id
     */
    @NotNull(message = "课程计划父级Id不能为空")
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    @NotNull(message = "层级不能为空")
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    @NotNull(message = "课程计划id不能为空")
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;

}
