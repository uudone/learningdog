package com.learningdog.learning.api;

import com.learningdog.base.model.PageResult;
import com.learningdog.learning.model.dto.MyCourseTableParams;
import com.learningdog.learning.po.CourseTable;
import com.learningdog.learning.service.CourseTableService;
import com.learningdog.learning.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
public class CourseTableController {

    @Resource
    CourseTableService  courseTableService;

    @ApiOperation("查询我的课程表")
    @GetMapping("/mycoursetable")
    public PageResult<CourseTable> getMyCourseTable(MyCourseTableParams params){
        String userId= SecurityUtils.getUserId();
        params.setUserId(userId);
        return courseTableService.getMyCourseTable(params);
    }


}
