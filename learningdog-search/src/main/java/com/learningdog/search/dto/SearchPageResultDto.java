package com.learningdog.search.dto;

import com.learningdog.base.model.PageResult;
import lombok.Data;

import java.util.List;

/**
 * @author: getjiajia
 * @description: 课程搜索结果dto
 * @version: 1.0
 */
@Data
public class SearchPageResultDto<T> extends PageResult {

    //大分类
    List<String> mtList;
    //小分类
    List<String> stList;

    public SearchPageResultDto(List<T>items,long counts,long page,long pageSize){
        super(items,counts,page,pageSize);
    }
    public SearchPageResultDto(){}
}
