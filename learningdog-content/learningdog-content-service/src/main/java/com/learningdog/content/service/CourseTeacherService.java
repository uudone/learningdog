package com.learningdog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.content.model.dto.AddCourseTeacherDto;
import com.learningdog.content.model.dto.EditCourseTeacherDto;
import com.learningdog.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author getjiajia
 */
public interface CourseTeacherService extends IService<CourseTeacher> {
    /**
     * @param companyId:
     * @param editCourseTeacherDto:
     * @return CourseTeacher
     * @author getjiajia
     * @description 新增课程教师信息
     */
    CourseTeacher insertTeacher(Long companyId,EditCourseTeacherDto editCourseTeacherDto);

    /**
     * @param courseId:
     * @return List<CourseTeacher>
     * @author getjiajia
     * @description 获取课程所有教师信息
     */
    List<CourseTeacher> getCourseTeachers(Long courseId);

    /**
     * @param companyId:
     * @param editCourseTeacherDto:
     * @return CourseTeacher
     * @author getjiajia
     * @description 修改课程教师信息
     */
    CourseTeacher updateTeacher(Long companyId,EditCourseTeacherDto editCourseTeacherDto);

    /**
     * @param companyId:
     * @param courseId:
     * @param teacherId:
     * @return void
     * @author getjiajia
     * @description 删除课程教师
     */
    void deleteTeacher(Long companyId,Long courseId,Long teacherId);

    /**
     * @param companyId:
     * @param editCourseTeacherDto:
     * @return CourseTeacher
     * @author getjiajia
     * @description 新增或修改课程教师信息
     */
    CourseTeacher insertOrUpdateTeacher(Long companyId, EditCourseTeacherDto editCourseTeacherDto);
}
