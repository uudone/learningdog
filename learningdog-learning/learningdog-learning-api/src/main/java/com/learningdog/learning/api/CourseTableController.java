package com.learningdog.learning.api;

import com.learningdog.learning.service.CourseTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("courseTable")
public class CourseTableController {

    @Autowired
    private CourseTableService  courseTableService;
}
