package com.learningdog.search.service;

import com.learningdog.base.model.PageParams;
import com.learningdog.search.dto.SearchCourseParamDto;
import com.learningdog.search.dto.SearchPageResultDto;
import com.learningdog.search.po.CourseIndex;

/**
 * @author: getjiajia
 * @description: 课程信息搜索service
 * @version: 1.0
 */
public interface SearchCourseService {

    /**
     * @param pageParams:
     * @param searchCourseParamDto:
     * @return SearchPageResultDto<CourseIndex>
     * @author getjiajia
     * @description 搜索课程列表
     */
    SearchPageResultDto<CourseIndex> queryCoursePublishIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto);
}
