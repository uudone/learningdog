package com.learningdog.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.learningdog.base.model.PageParams;
import com.learningdog.search.dto.SearchCourseParamDto;
import com.learningdog.search.dto.SearchPageResultDto;
import com.learningdog.search.po.CourseIndex;
import com.learningdog.search.service.SearchCourseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: getjiajia
 * @description: 课程搜索service实现类
 * @version: 1.0
 */
@Service
@Slf4j
public class SearchCourseServiceImpl implements SearchCourseService {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;
    @Value("${elasticsearch.course.source_fields}")
    private String sourceFields;
    @Resource
    RestHighLevelClient EsClient;

    @Override
    public SearchPageResultDto<CourseIndex> queryCoursePublishIndex(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {
        //创建搜索请求
        SearchRequest searchRequest=new SearchRequest(courseIndexStore);
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        BoolQueryBuilder booleanQueryBuilder= QueryBuilders.boolQuery();
        //source字段过滤
        String[] sourceFieldsArray=sourceFields.split(",");
        searchSourceBuilder.fetchSource(sourceFieldsArray,new String[]{});
        //关键字匹配
        if (searchCourseParamDto==null){
            searchCourseParamDto=new SearchCourseParamDto();
        }
        String keywords=searchCourseParamDto.getKeywords();
        if (StringUtils.isNotEmpty(keywords)){
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keywords, "name", "description", "companyName");
            //设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("60%");
            //提升name字段的boost值
            multiMatchQueryBuilder.field("name",10);
            booleanQueryBuilder.must(multiMatchQueryBuilder);
        }
        //条件过滤
        String mtName=searchCourseParamDto.getMt();
        String stName=searchCourseParamDto.getSt();
        String grade=searchCourseParamDto.getGrade();
        if (StringUtils.isNotEmpty(mtName)){
            booleanQueryBuilder.filter(QueryBuilders.termQuery("mtName",mtName));
        }
        if (StringUtils.isNotEmpty(stName)){
            booleanQueryBuilder.filter(QueryBuilders.termQuery("stName",stName));
        }
        if (StringUtils.isNotEmpty(grade)){
            booleanQueryBuilder.filter(QueryBuilders.termQuery("grade",grade));
        }
        //设置排序字段
        String orderType= searchCourseParamDto.getSortType();
        if (StringUtils.isNotEmpty(orderType)){
            //按价格升序排序
            if ("1".equals(orderType)){
                searchSourceBuilder.sort("price", SortOrder.ASC);
            }
            //按价格降序排序
            else if ("2".equals(orderType)) {
                searchSourceBuilder.sort("price", SortOrder.DESC);
            }

        }
        //分页设置
        long pageNo= pageParams.getPageNo();
        long pageSize=pageParams.getPageSize();
        long start=pageSize*(pageNo-1);
        searchSourceBuilder.from((int)start);
        searchSourceBuilder.size((int)pageSize);
        searchSourceBuilder.query(booleanQueryBuilder);
        //设置高亮字段
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);
        //聚合设置
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("mtAgg")
                        .field("mtName")
                        .size(30)
        );
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("stAgg")
                        .field("stName")
                        .size(30)
        );
        //发送请求
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse=null;
        try{
            searchResponse=EsClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("课程搜索异常：{}",e.getMessage());
            return new SearchPageResultDto<CourseIndex>(new ArrayList(),0,0,0);
        }
        //解析结果
        SearchPageResultDto<CourseIndex> pageResultDto=new SearchPageResultDto<>();
        List<CourseIndex> courseList = getCourseList(searchResponse);
        pageResultDto.setItems(courseList);
        List<String> mtList=getAggregation(searchResponse.getAggregations(),"mtAgg");
        List<String> stList=getAggregation(searchResponse.getAggregations(),"stAgg");
        pageResultDto.setMtList(mtList);
        pageResultDto.setStList(stList);
        pageResultDto.setPage(pageNo);
        pageResultDto.setPageSize(pageSize);
        pageResultDto.setCounts(searchResponse.getHits().getTotalHits().value);
        return pageResultDto;
    }


    /**
     * @param response:
     * @return List<CourseIndex>
     * @author getjiajia
     * @description 根据搜索结果封装课程信息
     */
    private List<CourseIndex> getCourseList(SearchResponse response){
        List<CourseIndex> list=new ArrayList<>();
        SearchHits hits = response.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit:searchHits){
            String sourceString=hit.getSourceAsString();
            CourseIndex courseIndex= JSON.parseObject(sourceString,CourseIndex.class);
            //取出高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields!=null){
                HighlightField field = highlightFields.get("name");
                if (field!=null){
                    Text[] fragments = field.getFragments();
                    StringBuffer stringBuffer=new StringBuffer();
                    for (Text str:fragments){
                        stringBuffer.append(str.toString());
                    }
                    courseIndex.setName(stringBuffer.toString());
                }
            }
            list.add(courseIndex);
        }
        return list;
    }

    /**
     * @param aggregations:
     * @param aggName:
     * @return List<String>
     * @author getjiajia
     * @description 获取聚合结果
     */
    private List<String> getAggregation(Aggregations aggregations,String aggName){
        Terms terms= aggregations.get(aggName);
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        List<String> list=new ArrayList<>();
        for(Terms.Bucket bucket: buckets){
            String key=bucket.getKeyAsString();
            list.add(key);
        }
        return list;
    }

}
