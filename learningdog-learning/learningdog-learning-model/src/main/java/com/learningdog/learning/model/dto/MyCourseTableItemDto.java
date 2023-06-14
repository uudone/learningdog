package com.learningdog.learning.model.dto;

import com.learningdog.learning.po.CourseTable;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: getjiajia
 * @description: 查询我的课程表返回结果dto
 * @version: 1.0
 */
@Data
public class MyCourseTableItemDto extends CourseTable {
    /**
     * 最近学习时间
     */
    private LocalDateTime learnDate;

    /**
     * 学习时长
     */
    private Long learnLength;

    /**
     * 章节id
     */
    private Long teachplanId;

    /**
     * 章节名称
     */
    private String teachplanName;
}
