package com.learningdog.base.exception;

import java.io.Serializable;

/**
 * @author: getjiajia
 * @description: 统一响应错误信息
 * @version: 1.0
 */
public class RestErrorResponse implements Serializable {
    private String errMessage;
    public RestErrorResponse(String errMessage){
        this.errMessage=errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
