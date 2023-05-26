package com.learningdog.content.mapper;

import com.learningdog.content.model.dto.TeachplanTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: getjiajia
 * @description: 教学计划表测试
 * @version: 1.0
 */
@SpringBootTest
public class TeachplanMapperTest {
    @Resource
    TeachplanMapper teachplanMapper;

    @Test
    public void testSelectTreeNodes(){
        Long courseId=25L;
        List<TeachplanTreeDto> teachplanTreeDtos = teachplanMapper.selectTreeNodes(courseId);
        System.out.println(teachplanTreeDtos);
    }
}
