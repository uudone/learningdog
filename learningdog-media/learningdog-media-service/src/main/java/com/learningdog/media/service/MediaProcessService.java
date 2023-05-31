package com.learningdog.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learningdog.media.model.po.MediaProcess;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author getjiajia
 * @since 2023-05-27
 */
public interface MediaProcessService extends IService<MediaProcess> {


    /**
     * @param shardIndex:
     * @param shardTotal:
     * @param count:
     * @return List<MediaProcess>
     * @author getjiajia
     * @description 获取待处理任务
     */
    List<MediaProcess> getTaskList(int shardIndex, int shardTotal, int count);

    /**
     * @param id:
     * @return boolean
     * @author getjiajia
     * @description 开启一个任务
     */
    boolean startTask(long id);


    /**
     * @param taskId:
     * @param status: 任务状态
     * @param fileId:
     * @param url:
     * @param errorMsg:
     * @return void
     * @author getjiajia
     * @description 保存任务结果
     */
    void saveProcessFinishStatus(Long taskId,String status,String fileId,String url,String errorMsg);
}
