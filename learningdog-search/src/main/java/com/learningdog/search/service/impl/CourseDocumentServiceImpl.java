package com.learningdog.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.search.service.CourseDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author: getjiajia
 * @description: 课程文档
 * @version: 1.0
 */
@Service
@Slf4j
public class CourseDocumentServiceImpl implements CourseDocumentService {

    @Resource
    RestHighLevelClient client;

    @Override
    public Boolean addCourseDocument(String indexName, String id, Object object) {
        IndexRequest request=new IndexRequest(indexName).id(id);
        request.source(JSON.toJSONString(object),XContentType.JSON);
        try{
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            String msg=e.getMessage();
            if (!msg.contains("200 OK")&&!msg.contains("201 Created")){
                log.error("添加文档出错：{}",e.getMessage());
                LearningdogException.cast("添加文档出错");
            }
        }
        return true;
    }

    @Override
    public Boolean updateCourseDocument(String indexName, String id, Object object) {
        UpdateRequest request=new UpdateRequest(indexName,id);
        request.doc(JSON.toJSONString(object), XContentType.JSON);
        try{
            client.update(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            String msg=e.getMessage();
            if (!msg.contains("200 OK")&&!msg.contains("201 Updated")) {
                log.error("更新文档出错：{}", e.getMessage());
                LearningdogException.cast("更新文档出错");
            }
        }
        return true;
    }

    @Override
    public Boolean deleteCourseDocument(String indexName, String id) {
        DeleteRequest request=new DeleteRequest(indexName,id);
        try{
            client.delete(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            String msg=e.getMessage();
            if (!msg.contains("200 OK")&&!msg.contains("201 Deleted")) {
                log.error("删除文档出错：{}", e.getMessage());
                LearningdogException.cast("删除文档出错");
            }
        }
        return true;
    }

}
