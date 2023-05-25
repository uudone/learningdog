package com.learningdog.content.service;

import com.learningdog.content.model.dto.CourseCategoryTreeDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: getjiajia
 * @description: CourseCategoryService测试类
 * @version: 1.0
 */
@SpringBootTest
public class CourseCategoryServiceTest {

    @Resource
    private CourseCategoryService courseCategoryService;

    @Test
    public void testQueryTreeNodes(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
