package com.learningdog.learning.model.dto;

import com.learningdog.content.po.Teachplan;
import com.learningdog.content.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

/**
 * @author: getjiajia
 * @description: 课程计划树形结构dto
 * @version: 1.0
 */
@Data
public class TeachplanTreeDto extends Teachplan {
    //课程计划关联的媒资信息
    TeachplanMedia teachplanMedia;

    //子节点
    List<TeachplanTreeDto> teachPlanTreeNodes;
}
