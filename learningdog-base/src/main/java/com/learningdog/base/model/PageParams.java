package com.learningdog.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: getjiajia
 * @description: 分页参数
 * @version: 1.0
 */
@Data
@ToString
public class PageParams {
    @ApiModelProperty("每页记录数默认值")
    private Long pageSize=10L;
    @ApiModelProperty("当前页码")
    private Long pageNo=1L;
    public PageParams(){}
    public PageParams(Long pageNo,Long pageSize){
        this.pageNo=pageNo;
        this.pageSize=pageSize;
    }
}
