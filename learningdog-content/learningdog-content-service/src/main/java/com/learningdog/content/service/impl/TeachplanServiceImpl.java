package com.learningdog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.content.mapper.TeachplanMapper;
import com.learningdog.content.model.dto.SaveTeachplanDto;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.po.Teachplan;
import com.learningdog.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class TeachplanServiceImpl extends ServiceImpl<TeachplanMapper, Teachplan> implements TeachplanService {

    @Resource
    TeachplanService teachplanService;

    @Resource
    TeachplanMapper teachplanMapper;

    @Override
    public List<TeachplanTreeDto> getTreeNodes(Long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto saveTeachplanDto) {
        Long id = saveTeachplanDto.getId();
        //如果id为null，新增数据
        if(id==null){
            Teachplan teachplan=new Teachplan();
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
//            int count = getTeachplanCount(teachplan.getCourseId(), teachplan.getParentid());
//            teachplan.setOrderby(count+1);
            Integer maxOrderby=teachplanMapper.selectMaxOrderby(teachplan.getCourseId(), teachplan.getParentid());
            maxOrderby=maxOrderby==null?0:maxOrderby;
            teachplan.setOrderby(maxOrderby+1);
            teachplan.setCreateDate(LocalDateTime.now());
            int insert=teachplanMapper.insert(teachplan);
            if(insert<=0){
                LearningdogException.cast("新增教学计划失败");
            }
        }else {//id不为null，修改课程计划
            Teachplan teachplan=teachplanMapper.selectById(id);
            if(teachplan==null){
                LearningdogException.cast("所需修改的课程计划不存在");
            }
            BeanUtils.copyProperties(saveTeachplanDto,teachplan);
            teachplan.setChangeDate(LocalDateTime.now());
            int update=teachplanMapper.updateById(teachplan);
            if(update<=0){
                LearningdogException.cast("修改课程计划失败");
            }
        }
    }

    @Override
    public void movedownTeachplan(Long teachplanId) {
        //获取当前教学计划信息
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //判断是否为空
        if(teachplan==null){
            LearningdogException.cast("当前教学计划不存在");
        }
        //获取同级目录的下一个教学计划
        Teachplan teachplanNext=teachplanMapper.selectNextTeachplan(teachplan.getCourseId(),teachplan.getParentid(),teachplan.getOrderby());
        //判断下一个教学计划是否为空
        if (teachplanNext==null){
            LearningdogException.cast("当前目录已移至最后");
        }
        //互换orderby字段值
        teachplanService.swapOrderby(teachplan,teachplanNext);
    }

    @Override
    @Transactional
    public void swapOrderby(Teachplan teachplan1,Teachplan teachplan2){
        int orderby1=teachplan1.getOrderby();
        int orderby2=teachplan2.getOrderby();
        LambdaUpdateWrapper<Teachplan> updateWrapper1=new LambdaUpdateWrapper<>();
        LambdaUpdateWrapper<Teachplan> updateWrapper2=new LambdaUpdateWrapper<>();
        updateWrapper1.eq(Teachplan::getId,teachplan1.getId())
                .set(Teachplan::getOrderby,orderby2);
        updateWrapper2.eq(Teachplan::getId,teachplan2.getId())
                .set(Teachplan::getOrderby,orderby1);
        int i=teachplanMapper.update(null,updateWrapper1);
        i+=teachplanMapper.update(null,updateWrapper2);
        if(i<=1){
            LearningdogException.cast("移动失败");
        }
    }

    @Override
    public void moveupTeachplan(Long teachplanId) {
        //获取当前教学计划信息
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //判断是否为空
        if(teachplan==null){
            LearningdogException.cast("当前教学计划不存在");
        }
        //获取同级目录下上一个课程计划信息
        Teachplan teachplanPre=teachplanMapper.selectPreTeachplan(teachplan.getCourseId(),teachplan.getParentid(),teachplan.getOrderby());
        //判断是否为空
        if (teachplanPre==null){
            LearningdogException.cast("当前目录已移至最前");
        }
        //互换orderby字段值
        teachplanService.swapOrderby(teachplan,teachplanPre);
    }

    @Override
    @Transactional
    public void deleteTeachplanById(Long teachplanId) {
        //查询课程信息是否存在
        Teachplan teachplan=teachplanMapper.selectById(teachplanId);
        if (teachplan==null){
            LearningdogException.cast("课程计划不存在");
        }
        //查询课程是否含有子节点
        LambdaQueryWrapper<Teachplan> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,teachplanId);
        queryWrapper.eq(Teachplan::getCourseId,teachplan.getCourseId());
        Integer count = teachplanMapper.selectCount(queryWrapper);
        //如果有子节点则不允许删除
        if (count>0){
            LearningdogException.cast("该课程目录下还有子节点，不允许删除");
        }else { //如果没有子节点则允许删除
            teachplanMapper.deleteById(teachplanId);
        }

    }


    /**
     * @param courseId:
     * @param parentId:
     * @return int
     * @author getjiajia
     * @description 查询同级目录的数量
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
