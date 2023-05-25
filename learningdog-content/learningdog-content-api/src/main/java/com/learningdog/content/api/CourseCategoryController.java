package com.learningdog.content.api;

import com.learningdog.content.model.dto.CourseCategoryTreeDto;
import com.learningdog.content.service.CourseCategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 课程分类 前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("course-category")
public class CourseCategoryController {

    @Resource
    private CourseCategoryService  courseCategoryService;

    @ApiOperation("课程类型树形结构查询接口")
    @GetMapping("/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return courseCategoryService.queryTreeNodes("1");
    }
}
