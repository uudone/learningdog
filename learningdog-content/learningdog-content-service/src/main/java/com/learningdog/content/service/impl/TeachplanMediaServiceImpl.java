package com.learningdog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.base.exception.LearningdogException;
import com.learningdog.content.mapper.TeachplanMapper;
import com.learningdog.content.mapper.TeachplanMediaMapper;
import com.learningdog.content.model.dto.BindTeachplanMediaDto;
import com.learningdog.content.po.Teachplan;
import com.learningdog.content.po.TeachplanMedia;
import com.learningdog.content.service.TeachplanMediaService;
import com.learningdog.feign.client.MediaClient;
import com.learningdog.media.po.MediaFiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class TeachplanMediaServiceImpl extends ServiceImpl<TeachplanMediaMapper, TeachplanMedia> implements TeachplanMediaService {

    @Resource
    TeachplanMediaMapper teachplanMediaMapper;

    @Resource
    TeachplanMapper teachplanMapper;

    @Resource
    MediaClient mediaClient;


    @Override
    @Transactional
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //获取教学计划的信息
        long teachplanId= bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        //教学计划是否为空
        if (teachplan==null){
            LearningdogException.cast("教学计划不存在");
        }
        //只允许二级教学计划绑定媒资文件
        if(teachplan.getGrade()!=2){
            LearningdogException.cast("只允许二级教学计划绑定媒资文件");
        }
        //获取媒资文件信息
        String mediaFilesId= bindTeachplanMediaDto.getMediaId();
        MediaFiles mediaFiles = mediaClient.getMediaFiles(mediaFilesId);
        //媒资文件是否为空
        if (mediaFiles==null){
            LearningdogException.cast("媒资文件不存在");
        }
        //删除原来和该教学计划绑定的信息
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId,teachplanId));
        //添加教学计划和媒资的绑定关系
        TeachplanMedia teachplanMedia=new TeachplanMedia();
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaId(mediaFilesId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        int insert = teachplanMediaMapper.insert(teachplanMedia);
        if (insert<=0){
            LearningdogException.cast("绑定媒资文件失败");
        }
        return teachplanMedia;
    }

    @Override
    @Transactional
    public void deleteAssociationMedia(Long teachplanId, String mediaId) {
        int delete=teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId,teachplanId)
                .eq(TeachplanMedia::getMediaId,mediaId));
        if (delete<=0){
            LearningdogException.cast("删除失败");
        }
    }

}
