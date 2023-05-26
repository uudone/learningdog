package com.learningdog.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author getjiajia
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * @param courseId:
     * @return List<TeachplanTreeDto>
     * @author getjiajia
     * @description 查询课程计划数据,并组成树形结构
     */
    List<TeachplanTreeDto> selectTreeNodes(Long courseId);

    /**
     * @param :
     * @return Integer
     * @author getjiajia
     * @description 获取课程计划表中同一层orderby字段最大值
     */
    Integer selectMaxOrderby(@Param("courseId") Long courseId,@Param("parentId") Long parentId);

    /**
     * @param courseId:
     * @param parentId:
     * @param orderby:
     * @return Teachplan
     * @author getjiajia
     * @description 获取同级目录下的下一个课程计划
     */
    Teachplan selectNextTeachplan(@Param("courseId")Long courseId,@Param("parentId")Long parentId,@Param("orderby")Integer orderby);

    /**
     * @param courseId:
     * @param parentId:
     * @param orderby:
     * @return Teachplan
     * @author getjiajia
     * @description 获取同级目录下的上一个课程计划
     */
    Teachplan selectPreTeachplan(@Param("courseId")Long courseId,@Param("parentId")Long parentId,@Param("orderby")Integer orderby);
}
