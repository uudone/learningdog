package com.learningdog.search.api;

import com.learningdog.search.po.CourseIndex;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: getjiajia
 * @description: 课程搜索接口
 * @version: 1.0
 */
@Api(value = "课程搜索接口",tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {



}
