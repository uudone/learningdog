package com.learningdog.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learningdog.media.mapper.MediaFilesMapper;
import com.learningdog.media.mapper.MediaProcessHistoryMapper;
import com.learningdog.media.mapper.MediaProcessMapper;
import com.learningdog.media.model.po.MediaFiles;
import com.learningdog.media.model.po.MediaProcess;
import com.learningdog.media.model.po.MediaProcessHistory;
import com.learningdog.media.service.MediaProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author getjiajia
 */
@Slf4j
@Service
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {

    @Resource
    MediaProcessMapper mediaProcessMapper;

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    public List<MediaProcess> getTaskList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal,shardIndex,count);
    }

    @Override
    public boolean startTask(long id) {
        return mediaProcessMapper.startTask(id)>0;
    }


    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        //查询任务信息
        MediaProcess mediaProcess=mediaProcessMapper.selectById(taskId);
        if (mediaProcess==null){
            return;
        }
        //如果任务处理失败
        if("3".equals(status)){
            LambdaQueryWrapper<MediaProcess> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(MediaProcess::getId,taskId);
            MediaProcess u_mediaProcess=new MediaProcess();
            u_mediaProcess.setStatus("3");
            u_mediaProcess.setErrormsg(errorMsg);
            u_mediaProcess.setFailCount(mediaProcess.getFailCount()+1);
            mediaProcessMapper.update(u_mediaProcess,queryWrapper);
            return;
        }
        //如果任务处理成功
        MediaFiles mediaFiles=mediaFilesMapper.selectById(fileId);
        if (mediaFiles==null){
            log.info("视频转码文件未记录：{}",fileId);
            throw new RuntimeException("视频转码文件未记录:"+fileId);
        }
        //更新媒资文件信息
        mediaFiles.setUrl(url);
        mediaFilesMapper.updateById(mediaFiles);
        //插入历史任务信息表
        mediaProcess.setUrl(url);
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        MediaProcessHistory mediaProcessHistory=new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess,mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        //删除任务记录表中的信息
        mediaProcessMapper.deleteById(taskId);
    }

}
