package com.learningdog.search.service;

import com.learningdog.search.po.CourseIndex;

/**
 * @author: getjiajia
 * @description: 课程文档service
 * @version: 1.0
 */
public interface CourseDocumentService {


    /**
     * @param indexName:
     * @param id:
     * @param object:
     * @return Boolean
     * @author getjiajia
     * @description 添加课程文档
     */
    Boolean addCourseDocument(String indexName,String id,Object object);

    /**
     * @param indexName:
     * @param id:
     * @param object:
     * @return Boolean
     * @author getjiajia
     * @description 更新课程文档
     */
    Boolean updateCourseDocument(String indexName,String id,Object object);


    /**
     * @param indexName:
     * @param id:
     * @return Boolean
     * @author getjiajia
     * @description 删除课程文档
     */
    Boolean deleteCourseDocument(String indexName,String id);

}
