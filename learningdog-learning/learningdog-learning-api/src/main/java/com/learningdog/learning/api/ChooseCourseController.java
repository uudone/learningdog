package com.learningdog.learning.api;

import com.learningdog.learning.model.dto.ChooseCourseDto;
import com.learningdog.learning.model.dto.CourseTableDto;
import com.learningdog.learning.service.ChooseCourseService;
import com.learningdog.learning.service.CourseTableService;
import com.learningdog.learning.util.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  选课信息前端控制器
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@RestController
@RequestMapping("/choosecourse")
public class ChooseCourseController {

    @Resource
    ChooseCourseService  chooseCourseService;
    @Resource
    CourseTableService courseTableService;

    @ApiOperation("添加选课")
    @PostMapping("/{courseId}")
    public ChooseCourseDto addChooseCourse(@PathVariable("courseId")Long courseId){
        String userId= SecurityUtils.getUserId();
        return chooseCourseService.addChooseCourse(userId,courseId);
    }

    @ApiOperation("获取学习资格")
    @PostMapping("/learnstatus/{courseId}")
    public CourseTableDto getLearnStatus(@PathVariable("courseId")Long courseId){
        String useId=SecurityUtils.getUserId();
        return courseTableService.getLearningStatus(useId,courseId);
    }
}
