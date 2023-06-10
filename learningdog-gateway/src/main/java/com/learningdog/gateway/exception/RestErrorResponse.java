package com.learningdog.gateway.exception;

import java.io.Serializable;

/**
 * @author: getjiajia
 * @description: 错误响应包装
 * @version: 1.0
 */
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }
}
