package com.learningdog.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.ChooseCoursePermission;
import com.learningdog.base.code.CoursePay;
import com.learningdog.base.code.CoursePublishStatus;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.base.model.RestResponse;
import com.learningdog.content.po.CoursePublish;
import com.learningdog.content.po.TeachplanMedia;
import com.learningdog.feign.client.ContentClient;
import com.learningdog.feign.client.MediaClient;
import com.learningdog.learning.mapper.LearnRecordMapper;
import com.learningdog.learning.model.dto.CourseTableDto;
import com.learningdog.learning.model.dto.TeachplanTreeDto;
import com.learningdog.learning.po.LearnRecord;
import com.learningdog.learning.service.CourseTableService;
import com.learningdog.learning.service.LearnRecordService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class LearnRecordServiceImpl extends ServiceImpl<LearnRecordMapper, LearnRecord> implements LearnRecordService {

    @Resource
    MediaClient mediaClient;
    @Resource
    ContentClient contentClient;
    @Resource
    CourseTableService courseTableService;
    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        //尝试免费获取课程资源
        RestResponse<String> freeResponse = getVideoFree(courseId, teachplanId, mediaId);
        if (!"请登录后购买课程再继续学习".equals(freeResponse.getMsg())){
            return freeResponse;
        }
        //判断学习资格
        CourseTableDto courseTableDto = courseTableService.getLearningStatus(userId, courseId);
        String learningStatus=courseTableDto.getLearnStatus();
        if (ChooseCoursePermission.TO_LEARN.equals(learningStatus)){
            return getMediaUrl(mediaId);
        }else if (ChooseCoursePermission.EXPIRED.equals(learningStatus)){
            RestResponse.validFail("您的选课已过期需要申请续期或重新支付");
        }
        return RestResponse.validFail("请购买课程再继续学习");
    }



    @Override
    public RestResponse<String> getVideoFree(Long courseId, Long teachplanId, String mediaId) {
        //查询课程发布信息
        CoursePublish coursePublish=contentClient.getCoursePublish(courseId);
        if (coursePublish==null){
            LearningdogException.cast("课程不存在或未发布");
        }
        if(CoursePublishStatus.OFFLINE.equals(coursePublish.getStatus())){
            LearningdogException.cast("课程已下架");
        }
        //获取课程计划信息
        String teachplanStr=coursePublish.getTeachplan();
        List<TeachplanTreeDto> treeNodes=JSON.parseArray(teachplanStr,TeachplanTreeDto.class);
        TeachplanTreeDto teachplanTreeDto = getOneByMediaIdAndTeachplanId(treeNodes, mediaId, teachplanId);
        if (teachplanTreeDto==null){
            return RestResponse.validFail("课程资源不存在");
        }
        //收费课程试学
        if ("1".equals(teachplanTreeDto.getIsPreview())){
            return getMediaUrl(mediaId);
        }
        //如果是免费课程直接获取
        if (CoursePay.FREE.equals(coursePublish.getCharge())){
           return getMediaUrl(mediaId);
        }
        return RestResponse.validFail("请登录后购买课程再继续学习");
    }


    private TeachplanTreeDto getOneByMediaIdAndTeachplanId(List<TeachplanTreeDto> treeNodes,String mediaId,Long teachplanId){
        //教学计划为两层结构
        for (TeachplanTreeDto treeNode : treeNodes) {
            List<TeachplanTreeDto> teachPlanTreeNodes = treeNode.getTeachPlanTreeNodes();
            for (TeachplanTreeDto teachPlanTreeNode : teachPlanTreeNodes) {
                TeachplanMedia teachplanMedia=teachPlanTreeNode.getTeachplanMedia();
                if (teachplanMedia!=null
                        &&mediaId.equals(teachplanMedia.getMediaId())
                        &&teachplanId.equals(teachplanMedia.getTeachplanId())){
                    return teachPlanTreeNode;
                }
            }

        }
        return null;
    }


    private RestResponse<String> getMediaUrl(String mediaId) {
        String restResponseStr = mediaClient.getPlayUrlByMediaId(mediaId);
        RestResponse<String> response= JSON.parseObject(restResponseStr,RestResponse.class);
        return response;
    }


}
