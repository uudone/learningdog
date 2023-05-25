package com.learningdog.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: getjiajia
 * @description: 分页查询结果
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PageResult<T> implements Serializable {
    //数据列表
    private List<T> items;

    //总数
    private long counts;

    //当前页码
    private long page;

    //每页记录数
    private long pageSize;
}
