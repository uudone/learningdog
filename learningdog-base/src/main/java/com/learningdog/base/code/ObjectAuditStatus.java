package com.learningdog.base.code;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author: getjiajia
 * @description: 对象审核状态
 * @version: 1.0
 */
public interface ObjectAuditStatus {
    //审核未通过
    String FAILED="002001";
    //未审核
    String UNDO="002002";
    //审核通过
    String SUCCESS="002003";

}
