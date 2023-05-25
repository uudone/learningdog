package com.learningdog.base.code;

/**
 * @author: getjiajia
 * @description: 课程的审核状态
 * @version: 1.0
 */
public interface CourseAuditStatus {
    //审核未通过
    String FAILED="202001";
    //未提交
    String UN_SUBMITTED="202002";
    //已提交
    String SUBMITTED="202003";
    //审核通过
    String SUCCESS="202004";
}
