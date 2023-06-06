package com.learningdog.search.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: getjiajia
 * @description: 课程信息搜索参数dto
 * @version: 1.0
 */
@Data
public class SearchCourseParamDto{
    @ApiModelProperty("搜索关键字")
    private String keywords;
    @ApiModelProperty("大分类名称")
    private String mt;
    @ApiModelProperty("小分类名称")
    private String st;
    @ApiModelProperty("难度等级")
    private String grade;
    @ApiModelProperty("排序字段，1为价格升序，2为价格降序")
    private String sortType;

}
