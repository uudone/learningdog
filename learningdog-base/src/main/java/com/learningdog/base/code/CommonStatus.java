package com.learningdog.base.code;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author: getjiajia
 * @description: 公共属性类型
 * @version: 1.0
 */
public interface CommonStatus {
    //使用态
    String USE="1";
    //删除态
    String DELETE="0";
    //暂时态
    String PAUSE="-1";
}
