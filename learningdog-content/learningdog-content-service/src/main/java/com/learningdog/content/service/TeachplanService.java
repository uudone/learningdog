package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.model.dto.SaveTeachplanDto;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author getjiajia
 */
public interface TeachplanService extends IService<Teachplan> {

    /**
     * @param courseId:  课程id
     * @return List<TeachplanTreeDto>
     * @author getjiajia
     * @description 获取课程计划树形结构信息
     */
    List<TeachplanTreeDto> getTreeNodes(Long courseId);

    /**
     * @param saveTeachplanDto:
     * @return void
     * @author getjiajia
     * @description 新增或修改课程计划
     */
    void saveTeachplan(SaveTeachplanDto saveTeachplanDto);

    /**
     * @param teachplanId:
     * @return void
     * @author getjiajia
     * @description 将教学计划下移一位
     */
    void movedownTeachplan(Long teachplanId);

    /**
     * @param teachplan1:
     * @param teachplan2:
     * @return void
     * @author getjiajia
     * @description 互换两个课程计划的orderby字段值
     */
    void swapOrderby(Teachplan teachplan1, Teachplan teachplan2);

    /**
     * @param teachplanId:
     * @return void
     * @author getjiajia
     * @description 将教学计划上移一位
     */
    void moveupTeachplan(Long teachplanId);

    /**
     * @param teachplanId:
     * @return void
     * @author getjiajia
     * @description 根据课程计划id删除
     */
    void deleteTeachplanById(Long teachplanId);
}
