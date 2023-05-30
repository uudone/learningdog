package com.learningdog.base.model;

import lombok.Data;

/**
 * @author: getjiajia
 * @description: 通用结果类
 * @version: 1.0
 */
@Data
public class RestResponse<T> {
    //响应编码，0为正常，-1为错误
    private int code;

    //响应提示信息
    private String msg;

    //响应内容
    private T result;

    public RestResponse(){
        this(0,"success");
    }
    public RestResponse(int code,String msg){
        this.code=code;
        this.msg=msg;
    }

    /**
     * @param msg:
     * @return RestResponse<T>
     * @author getjiajia
     * @description 错误信息封装
     */
    public static <T> RestResponse<T> validFail(String msg){
        RestResponse<T> restResponse=new RestResponse<T>();
        restResponse.setCode(-1);
        restResponse.setMsg(msg);
        return restResponse;
    }

    public static <T> RestResponse<T> validFail(T result,String msg){
        RestResponse<T> restResponse = validFail(msg);
        restResponse.setResult(result);
        return restResponse;
    }


    public static <T> RestResponse<T> success(){
        return new RestResponse<>();
    }

    public static <T> RestResponse<T> success(T result){
        RestResponse<T> restResponse=success();
        restResponse.setResult(result);
        return restResponse;
    }


    public Boolean isSuccessful(){
        return code==0;
    }

}
