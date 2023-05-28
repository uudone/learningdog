package com.learningdog.media.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: getjiajia
 * @description: 媒资文件查询条件dto
 * @version: 1.0
 */
@Data
@ApiModel(value = "QueryMediaParamsDto",description = "媒资文件查询条件")
public class QueryMediaParamsDto {
    @ApiModelProperty("媒资文件名称")
    private String filename;
    @ApiModelProperty("媒资类型")
    private String fileType;
    @ApiModelProperty("审核状态")
    private String auditStatus;

}
