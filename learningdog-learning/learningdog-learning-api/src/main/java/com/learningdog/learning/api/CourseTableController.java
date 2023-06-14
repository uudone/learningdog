package com.learningdog.learning.api;

import com.learningdog.learning.service.CourseTableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  我的课程表前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@Api("我的课程表接口")
@RequestMapping("courseTable")
public class CourseTableController {

    @Resource
    CourseTableService  courseTableService;


}
