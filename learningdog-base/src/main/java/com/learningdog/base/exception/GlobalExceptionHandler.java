package com.learningdog.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: getjiajia
 * @description: 全局异常处理器
 * @version: 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(LearningdogException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse myException(LearningdogException e){
        log.error("【系统异常】{}",e.getErrorMessage(),e);
        return new RestErrorResponse(e.getErrorMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        List<String> msgList=new ArrayList<>();
        bindingResult.getFieldErrors().stream()
                .forEach(item->msgList.add(item.getDefaultMessage()));
        String errMessage= StringUtils.join(msgList,"，");
        log.error("【系统异常】{}",errMessage);
        return new RestErrorResponse(errMessage);
    }


    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse otherException(Exception e){
        if (e==null){
            log.error("【系统异常】{}",e);
        }else {
            log.error("【系统异常】{}",e.getMessage(),e);
            //org.springframework.security.access.AccessDeniedException: 不允许访问
            if(e.getMessage().equals("不允许访问")){
                return new RestErrorResponse("没有操作此功能的权限");
            }
        }
        return new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrorMessage());
    }



}
