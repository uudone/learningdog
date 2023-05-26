package com.learningdog.base.exception;

/**
 * @author: getjiajia
 * @description: 通用错误信息
 * @version: 1.0
 */
public enum CommonError {
    UNKNOWN_ERROR("执行过程异常，请重试。"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空");
    private String errorMessage;

    public String getErrorMessage(){
        return errorMessage;
    }

    private CommonError(String errorMessage){
        this.errorMessage=errorMessage;
    }

}
