package com.learningdog.base.exception;

/**
 * @author: getjiajia
 * @description: 学习汪项目异常类
 * @version: 1.0
 */
public class LearningdogException extends RuntimeException{
    private String errorMessage;

    public LearningdogException(){
        super();
    }
    public LearningdogException(String errorMessage){
        super(errorMessage);
        this.errorMessage=errorMessage;
    }
    public String getErrorMessage(){
        return errorMessage;
    }
    public static void cast(CommonError commonError){
        throw new LearningdogException(commonError.getErrorMessage());
    }
    public static void cast(String errorMessage){
        throw new LearningdogException(errorMessage);
    }
}
