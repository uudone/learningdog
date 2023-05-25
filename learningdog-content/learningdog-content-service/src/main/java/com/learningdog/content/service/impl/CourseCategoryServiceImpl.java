package com.learningdog.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.content.mapper.CourseCategoryMapper;
import com.learningdog.content.model.dto.CourseCategoryTreeDto;
import com.learningdog.content.model.po.CourseCategory;
import com.learningdog.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程分类 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl extends ServiceImpl<CourseCategoryMapper, CourseCategory> implements CourseCategoryService {

    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos=courseCategoryMapper.selectTreeNodes(id);
        //使用Map存储节点，方便后续使用id获取节点
        Map<String,CourseCategoryTreeDto> treeMap=courseCategoryTreeDtos.stream()
                .filter(item->!id.equals(item.getId()))
                .collect(Collectors.toMap(
                        key -> key.getId(),
                        value -> value,
                        (key1,key2)->key2
                ));
        List<CourseCategoryTreeDto> results=new ArrayList<>();
        //遍历每个元素，找到如果其父节点不为空，设置父节点的子节点
        courseCategoryTreeDtos.stream()
                .filter(item->!id.equals(item.getId()))
                .forEach(
                    item->{
                        if(id.equals(item.getParentid())){
                            results.add(item);
                        }
                        //找到当前节点父节点
                        CourseCategoryTreeDto parent=treeMap.get(item.getParentid());
                        if(parent!=null){
                            if(parent.getChildrenTreeNodes()==null){
                                parent.setChildrenTreeNodes(new ArrayList<>());
                            }
                            parent.getChildrenTreeNodes().add(item);
                        }
                    }
                );
        return results;
    }
}
