package com.learningdog.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.CourseAuditStatus;
import com.learningdog.base.code.CoursePublishStatus;
import com.learningdog.base.exception.CommonError;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.content.mapper.CoursePublishMapper;
import com.learningdog.content.mapper.CoursePublishPreMapper;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.po.CourseMarket;
import com.learningdog.content.po.CoursePublish;
import com.learningdog.content.po.CoursePublishPre;
import com.learningdog.content.service.CourseBaseService;
import com.learningdog.content.service.CoursePublishService;
import com.learningdog.feign.client.MediaClient;
import com.learningdog.feign.conf.MultipartSupportConfig;
import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 课程发布 服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class CoursePublishServiceImpl extends ServiceImpl<CoursePublishMapper, CoursePublish> implements CoursePublishService {

    @Resource
    CoursePublishMapper coursePublishMapper;
    @Resource
    CoursePublishPreMapper coursePublishPreMapper;
    @Resource
    CourseBaseService courseBaseService;
    @Resource
    CoursePublishService coursePublishService;
    @Resource
    MqMessageService mqMessageService;
    @Resource
    MediaClient mediaClient;

    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
        //查询课程预发布表
        CoursePublishPre coursePublishPre=coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre==null){
            LearningdogException.cast("该课程未提交审核或不存在");
        }
        //本机构只能发布本机构的课程
        if (!companyId.equals(coursePublishPre.getCompanyId())){
            LearningdogException.cast("本机构只能发布本机构的课程");
        }
        //课程审核状态为通过时才可以发布
        if (!CourseAuditStatus.SUCCESS.equals(coursePublishPre.getStatus())){
            LearningdogException.cast("该课程审核不通过或未经审核");
        }
        //保存课程发布信息
        CoursePublish coursePublish=new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setCreateDate(LocalDateTime.now());
        coursePublish.setStatus(CoursePublishStatus.PUBLISHED);
        //查询课程发布表中是否有该课程
        CoursePublish coursePublishFromDB=coursePublishMapper.selectById(courseId);
        if (coursePublishFromDB==null){
            int insert=coursePublishMapper.insert(coursePublish);
            if (insert<=0){
                log.error("插入到CoursePublish表失败，id={}",courseId);
                LearningdogException.cast("发布课程失败");
            }
        }else {
            int update=coursePublishMapper.updateById(coursePublish);
            if (update<=0){
                log.error("更新CoursePublish表失败，id={}",courseId);
                LearningdogException.cast("发布课程失败");
            }
        }
        //修改课程基本信息表的发布状态
        courseBaseService.updatePublishStatus(courseId,CoursePublishStatus.PUBLISHED);
        //保存到消息处理表中
        coursePublishService.saveCoursePublishMessage(courseId);
        //删除预发布课程表中的记录
        int delete = coursePublishPreMapper.deleteById(courseId);
        if (delete<=0){
            log.error("删除CoursePublishPre表中的记录失败，id={}",courseId);
            LearningdogException.cast("发布课程失败");
        }

    }

    @Override
    @Transactional
    public void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage=mqMessageService.addMessage("course_publish",String.valueOf(courseId),null,null);
        if (mqMessage==null){
            LearningdogException.cast(CommonError.UNKNOWN_ERROR);
        }
    }

    @Override
    public File generateCourseHtml(long courseId){
        File htmlFile=null;
        InputStream inputStream=null;
        FileOutputStream outputStream=null;
        try{
            Configuration configuration=new Configuration(Configuration.getVersion());
            String classpath=this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath+"/templates/"));
            configuration.setDefaultEncoding("utf-8");
            //获取指定模板文件
            Template template=configuration.getTemplate("course_template.ftl");
            //准备数据
            CoursePreviewDto coursePreviewDto=new CoursePreviewDto();
            CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
            if (coursePublish==null){
                log.error("课程静态化失败，未找到课程发布信息，courseId：",courseId);
                throw new RuntimeException("课程静态化失败，未找到课程发布信息，courseId："+courseId);
            }
            //课程基本信息
            CourseBaseInfoDto courseBaseInfoDto=new CourseBaseInfoDto();
            BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
            //课程营销信息
            CourseMarket courseMarket= JSON.parseObject(coursePublish.getMarket(),CourseMarket.class);
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
            coursePreviewDto.setCourseBase(courseBaseInfoDto);
            //课程计划
            String teachplans=coursePublish.getTeachplan();
            coursePreviewDto.setTeachplans(JSON.parseObject(teachplans, List.class));
            //课程教师
            String teachers=coursePublish.getTeachers();
            coursePreviewDto.setTeachers(JSON.parseObject(teachers,List.class));

            HashMap<String, Object> map = new HashMap<>();
            map.put("model",coursePreviewDto);
            //静态化
            String content= FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
            inputStream= IOUtils.toInputStream(content);
            //创建临时文件
            htmlFile=File.createTempFile("course",".html");
            log.debug("课程静态化，生成静态文件:{}",htmlFile.getAbsolutePath());
            outputStream=new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream,outputStream);
        }catch (IOException e){
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(long courseId, File file) {
        try{
            MultipartFile multipartFile= MultipartSupportConfig.getMultipartFile(file);
            String course=mediaClient.uploadFile(multipartFile,null,courseId+".html");
            if (course==null){
                throw new RuntimeException("上传课程静态文件异常");
            }
        } finally {
            if (file!=null){
                file.delete();
            }
        }

    }
}
