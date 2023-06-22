package com.learningdog.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.code.CourseAuditStatus;
import com.learningdog.base.code.CoursePublishStatus;
import com.learningdog.base.exception.CommonError;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.content.mapper.CoursePublishMapper;
import com.learningdog.content.mapper.CoursePublishPreMapper;
import com.learningdog.content.model.dto.CourseBaseInfoDto;
import com.learningdog.content.model.dto.CoursePreviewDto;
import com.learningdog.content.model.dto.CoursePublishDto;
import com.learningdog.content.model.dto.TeachplanTreeDto;
import com.learningdog.content.po.CourseMarket;
import com.learningdog.content.po.CoursePublish;
import com.learningdog.content.po.CoursePublishPre;
import com.learningdog.content.po.CourseTeacher;
import com.learningdog.content.service.CourseBaseService;
import com.learningdog.content.service.CoursePublishService;
import com.learningdog.feign.client.MediaClient;
import com.learningdog.feign.client.SearchClient;
import com.learningdog.feign.conf.MultipartSupportConfig;
import com.learningdog.messagesdk.po.MqMessage;
import com.learningdog.messagesdk.service.MqMessageService;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    SearchClient searchClient;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    RedissonClient redissonClient;

    RBloomFilter<Long> bloomFilter;

    @PostConstruct
    public void init(){
        bloomFilter=redissonClient.getBloomFilter("course_publish",new JsonJacksonCodec());
        this.refreshBloom(bloomFilter);
    }

    /**
     * @param bloomFilter:
     * @return void
     * @author getjiajia
     * @description 重新加载布隆过滤器中的数据
     */
    private void refreshBloom(RBloomFilter<Long> bloomFilter) {
        bloomFilter.delete();
        bloomFilter.tryInit(100000L,0.03);
        List<Long> courseIdList = this.list(new LambdaQueryWrapper<CoursePublish>()
                .select(CoursePublish::getId))
                .stream().map(CoursePublish::getId).collect(Collectors.toList());
        courseIdList.forEach(bloomFilter::add);
    }

    @Override
    @Transactional
    public void publish(Long companyId, Long courseId,String token) {
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
        coursePublish.setOnlineDate(LocalDateTime.now());
        coursePublish.setStatus(CoursePublishStatus.PUBLISHED);
        //查询课程发布表中是否有该课程
        CoursePublish coursePublishFromDB=coursePublishMapper.selectById(courseId);
        if (coursePublishFromDB==null){
            coursePublish.setCreateDate(LocalDateTime.now());
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
        coursePublishService.saveCoursePublishMessage(courseId,companyId,token);
        //删除预发布课程表中的记录
        int delete = coursePublishPreMapper.deleteById(courseId);
        if (delete<=0){
            log.error("删除CoursePublishPre表中的记录失败，id={}",courseId);
            LearningdogException.cast("发布课程失败");
        }

    }

    @Override
    @Transactional
    public void saveCoursePublishMessage(Long courseId,Long companyId,String token) {
        MqMessage mqMessage=mqMessageService.addMessage("course_publish",String.valueOf(courseId),String.valueOf(companyId),token);
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
            configuration.setTemplateLoader(new ClassTemplateLoader(this.getClass().getClassLoader(),"/templates"));
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
    public void uploadCourseHtml(long companyId,long courseId, File file,String token) {
        try{
            MultipartFile multipartFile= MultipartSupportConfig.getMultipartFile(file);
            String course=mediaClient.uploadFile(multipartFile,null,courseId+".html",token);
            if (course==null){
                throw new RuntimeException("上传课程静态文件异常");
            }
        } finally {
            if (file!=null){
                file.delete();
            }
        }
    }

    @Override
    @Transactional
    public void offline(Long companyId, Long courseId) {
        //查询课程发布表
        CoursePublish coursePublish=coursePublishMapper.selectById(courseId);
        if (coursePublish==null){
            LearningdogException.cast("该课程未发布");
        }
        if(CoursePublishStatus.OFFLINE.equals(coursePublish.getStatus())){
            LearningdogException.cast("该课程已下线");
        }
        //本机构只能下架本机构的课程
        if (!companyId.equals(coursePublish.getCompanyId())){
            LearningdogException.cast("本机构只能下架本机构的课程");
        }
        //修改课程发布状态为已下线
        courseBaseService.updatePublishStatus(courseId,CoursePublishStatus.OFFLINE);
        coursePublishMapper.update(null,new LambdaUpdateWrapper<CoursePublish>()
                .eq(CoursePublish::getId,courseId)
                .set(CoursePublish::getStatus,CoursePublishStatus.OFFLINE)
                .set(CoursePublish::getOfflineDate,LocalDateTime.now()));
        //修改课程审核状态为未提交
        courseBaseService.updateAuditStatus(courseId,CourseAuditStatus.UN_SUBMITTED);
        //删除es中的索引记录
        Boolean delete = searchClient.delete(courseId);
        if (!delete){
            log.info("删除课程索引记录失败，courseId：{}",courseId);
            LearningdogException.cast("课程下架失败，请重试");
        }
    }

    @Override
    public CoursePublishDto getCoursePublishInfo(Long courseId) {
        CoursePublish coursePublish=getCoursePublishFromCache(courseId);
        if (coursePublish==null){
            LearningdogException.cast("查询的课程不存在或未发布");
        }
        //获取课程基本信息和营销信息
        CourseBaseInfoDto courseBaseInfoDto=new CourseBaseInfoDto();
        BeanUtils.copyProperties(coursePublish,courseBaseInfoDto);
        String courseMarketStr=coursePublish.getMarket();
        CourseMarket courseMarket=JSON.parseObject(courseMarketStr,CourseMarket.class);
        courseBaseInfoDto.setQq(courseMarket.getQq());
        courseBaseInfoDto.setWechat(courseMarket.getWechat());
        courseBaseInfoDto.setPhone(courseMarket.getPhone());
        //获取课程计划信息
        String teachplanStr=coursePublish.getTeachplan();
        List<TeachplanTreeDto> treeNodes=JSON.parseArray(teachplanStr,TeachplanTreeDto.class);
        //获取课程教师信息
        String teacherStr=coursePublish.getTeachers();
        List<CourseTeacher> teachers=JSON.parseArray(teacherStr,CourseTeacher.class);
        //封装返回数据
        CoursePublishDto coursePublishDto=new CoursePublishDto();
        coursePublishDto.setCourseBase(courseBaseInfoDto);
        coursePublishDto.setTeachplans(treeNodes);
        coursePublishDto.setTeachers(teachers);
        return coursePublishDto;
    }

    @Override
    public CoursePublish getCoursePublishFromCache(Long courseId) {
        boolean contains = bloomFilter.contains(courseId);
        if (contains){
            Object jsonObj = redisTemplate.opsForValue().get("course:publish:" + courseId);
            if (jsonObj!=null){
                String jsonString=jsonObj.toString();
                log.debug("从redis中获取课程发布信息：{}",jsonString);
                return JSON.parseObject(jsonString,CoursePublish.class);
            }
            //获取分布式锁
            RLock lock = redissonClient.getLock("coursequerylock:" + courseId);
            lock.lock();
            try{
                //获取到锁之后先查询从缓存中是否已经存在数据
                jsonObj = redisTemplate.opsForValue().get("course:publish:" + courseId);
                if (jsonObj!=null){
                    String jsonString=jsonObj.toString();
                    log.debug("从redis中获取课程发布信息：{}",jsonString);
                    return JSON.parseObject(jsonString,CoursePublish.class);
                }
                //从数据库中查询数据
                CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
                log.debug("从数据库中查询课程发布信息：{}",coursePublish);
                //将数据存入redis
                if (coursePublish!=null){
                    redisTemplate.opsForValue().set("course:publish:"+courseId,JSON.toJSONString(coursePublish),30, TimeUnit.MINUTES);
                }
                return coursePublish;
            }finally {
                //释放锁
                lock.unlock();
            }

        }
        log.debug("布隆过滤器不存在课程id：{}",courseId);
        return null;
    }

    @Override
    public void addCourseIdToBloomFilter(Long courseId) {
        bloomFilter.add(courseId);
    }


}
