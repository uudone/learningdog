package com.learningdog.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.content.mapper.CoursePublishPreMapper;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.po.CoursePublishPre;
import com.learningdog.content.service.CourseBaseService;
import com.learningdog.content.service.CoursePublishPreService;
import com.learningdog.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class CoursePublishPreServiceImpl extends ServiceImpl<CoursePublishPreMapper, CoursePublishPre> implements CoursePublishPreService {

    @Resource
    CourseBaseService courseBaseService;
    @Resource
    TeachplanService teachplanService;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //获取课程基本信息和营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseService.getCourseBaseById(courseId);
        //获取课程计划信息
        List<TeachplanTreeDto> treeNodes = teachplanService.getTreeNodes(courseId);
        //封装返回信息
        CoursePreviewDto coursePreviewDto=new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(treeNodes);
        return coursePreviewDto;
    }
}
