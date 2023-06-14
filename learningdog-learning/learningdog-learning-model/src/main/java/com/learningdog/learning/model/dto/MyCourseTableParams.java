package com.learningdog.learning.model.dto;

import lombok.Data;

/**
 * @author: getjiajia
 * @description: 查询我的课程表查询参数
 * @version: 1.0
 */
@Data
public class MyCourseTableParams {
    private String userId;

    //课程类型  [{"code":"700001","desc":"免费课程"},{"code":"700002","desc":"收费课程"}]
    private String courseType;

    //排序 1按学习时间进行排序 2按加入时间进行排序
    private String sortType;

    //1即将过期、2已经过期
    private String expiresType;

    private int page=1;
    private int startIndex=0;
    private int size=4;
}
