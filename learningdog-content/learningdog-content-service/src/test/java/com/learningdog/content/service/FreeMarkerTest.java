package com.learningdog.content.service;

import com.alibaba.fastjson.JSON;
import com.learningdog.content.mapper.CoursePublishMapper;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.po.CourseBase;
import com.learningdog.content.po.CourseMarket;
import com.learningdog.content.po.CoursePublish;
import com.learningdog.content.po.Teachplan;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: getjiajia
 * @description: 静态化页面测试
 * @version: 1.0
 */
@SpringBootTest
public class FreeMarkerTest {

    @Resource
    CoursePublishMapper coursePublishMapper;

    @Test
    public void testGenerateHtmlTemplate() throws IOException, TemplateException {
        Configuration configuration=new Configuration(Configuration.getVersion());
        String classpath=this.getClass().getResource("/").getPath();
        configuration.setDirectoryForTemplateLoading(new File(classpath+"/templates/"));
        configuration.setDefaultEncoding("utf-8");
        Template template=configuration.getTemplate("course_template.ftl");
        //数据
        CoursePreviewDto coursePreviewDto=new CoursePreviewDto();
        CoursePublish coursePublish = coursePublishMapper.selectById(1L);
        //课程基本信息
        CourseBaseInfoDto courseBaseInfoDto=new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
        //课程营销信息
        String market=coursePublish.getMarket();
        CourseMarket courseMarket=JSON.parseObject(market,CourseMarket.class);
        BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        //课程计划
        String teachplan = coursePublish.getTeachplan();
        coursePreviewDto.setTeachplans(JSON.parseObject(teachplan,List.class));
        //课程教师
        String teachers=coursePublish.getTeachers();
        coursePreviewDto.setTeachers(JSON.parseObject(teachers,List.class));
        Map<String,Object> map=new HashMap<>();
        map.put("model",coursePreviewDto);
        //静态化
        String content= FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
        System.out.println("content:"+content);
        //将静态化内容输出到文件中
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream outputStream=new FileOutputStream("C:\\Users\\LIJIAHAO\\Desktop\\test.html");
        IOUtils.copy(inputStream,outputStream);
    }
}
